package com.vn.ntsc.ui.profile.edit;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.SubscriberCallback;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.editprofile.EditProfileRequest;
import com.vn.ntsc.repository.model.editprofile.EditProfileResponse;
import com.vn.ntsc.repository.model.editprofile.UploadImageResponse;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.signup.SignUpByFacebookRequest;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.remote.ApiMediaService;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by dev22 on 8/21/17.
 */
class EditProfilePresenter extends BasePresenter<EditProfileContract.View> implements EditProfileContract.Presenter {
    private final ApiMediaService imageUploadService;

    @Inject
    EditProfilePresenter(ApiMediaService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @Override
    public void editProfile(final EditProfileRequest editProfileRequest, String token, String sum, String path) {
        view.showLoadingDialog();

        //File creating from selected URL
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        addSubscriber(
                // upload image first
                imageUploadService.uploadAvatar(token, sum, requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // check upload success or not ?
                        .filter(new Predicate<UploadImageResponse>() {
                            @Override
                            public boolean test(@NonNull UploadImageResponse uploadImageResponse) throws Exception {
                                switch (uploadImageResponse.code) {
                                    case ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN:
                                        view.onInvalidToken();
                                        break;
                                    case ServerResponse.DefinitionCode.SERVER_UPLOAD_FILE_ERROR:
                                        view.onUploadAvatarFail();
                                        break;
                                    case ServerResponse.DefinitionCode.SERVER_AGE_DENY:
                                    case ServerResponse.DefinitionCode.SERVER_INVALID_BIRTHDAY:
                                        view.onInvalidBirthday();
                                        break;
                                    default:
                                        // uploadImageResponse.code == ServerResponse.DefinitionCode.SERVER_SUCCESS
                                        // attach avatar id if upload success
                                        editProfileRequest.avatarId = uploadImageResponse.data.imageId;
                                        break;
                                }
                                return uploadImageResponse.code == ServerResponse.DefinitionCode.SERVER_SUCCESS;
                            }
                        })
                        // error often IOException or something else => go ahead
                        .onErrorReturn(new Function<Throwable, UploadImageResponse>() {
                            @Override
                            public UploadImageResponse apply(@NonNull Throwable throwable) throws Exception {
                                return new UploadImageResponse(ServerResponse.DefinitionCode.SERVER_UPLOAD_FILE_ERROR, null);
                            }
                        })
                        // continue update profile
                        .flatMap(new Function<UploadImageResponse, ObservableSource<EditProfileResponse>>() {
                            @Override
                            public ObservableSource<EditProfileResponse> apply(@NonNull UploadImageResponse uploadImageResponse) throws Exception {
                                return apiService.updateProfile(editProfileRequest)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());
                            }
                        })
                        // check server valid token ?
                        .filter(new Predicate<EditProfileResponse>() {
                            @Override
                            public boolean test(@NonNull EditProfileResponse editProfileResponse) throws Exception {
                                switch (editProfileResponse.code){
                                    case ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN:
                                        view.onInvalidToken();
                                        break;
                                    case ServerResponse.DefinitionCode.SERVER_WRONG_DATA_FORMAT:
                                        view.onServerResponseInvalid(editProfileResponse.code, null);
                                        break;
                                    case ServerResponse.DefinitionCode.SERVER_INVALID_BIRTHDAY:
                                    case ServerResponse.DefinitionCode.SERVER_AGE_DENY:
                                        view.onInvalidBirthday();
                                        break;
                                }
                                return true;
                            }
                        })
                        // if success update profile -> go to main
                        .subscribeWith(new SubscriberCallback<EditProfileResponse>(view) {
                            @Override
                            public void onSuccess(EditProfileResponse response) {
                                view.gotoMain(response.data);
                            }

                            @Override
                            public void onCompleted() {
                                view.hideLoadingDialog();
                            }
                        })
        );
    }

    @Override
    public void requestUserInfo(final UserInfoRequest request) {

        addSubscriber(apiService.getUserInfo(request)
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribeOn(Schedulers.io())
                .subscribeWith(new SubscriberCallback<GetUserInfoResponse>(view) {
                    @Override
                    public void onSuccess(GetUserInfoResponse response) {
                        if (response != null) {
                            view.onLoadUserInfoSuccess(response.data);
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }
                }));
    }

    @Override
    public void signUpFabook(SignUpByFacebookRequest signUpByFacebookRequest) {
        addSubscriber(
                apiService.signUpFacebook(signUpByFacebookRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        // check server valid token ?
                        .filter(new Predicate<LoginResponse>() {
                            @Override
                            public boolean test(@NonNull LoginResponse loginResponse) throws Exception {
                                if (loginResponse.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN) {
                                    view.onInvalidToken();
                                    return false;
                                }
                                return true;
                            }
                        })
                        // if success update profile -> go to main
                        .subscribeWith(new SubscriberCallback<LoginResponse>(view) {
                            @Override
                            public void onSuccess(LoginResponse response) {
                                if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS)
                                    view.gotoMain(response.authenData);
                            }

                            @Override
                            public void onCompleted() {
                                view.hideLoadingDialog();
                            }
                        })
        );
    }
}