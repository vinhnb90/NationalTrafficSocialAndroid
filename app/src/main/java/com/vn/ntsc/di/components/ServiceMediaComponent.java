package com.vn.ntsc.di.components;

import com.vn.ntsc.di.dependencies.MediaComponent;
import com.vn.ntsc.di.modules.ServiceModule;
import com.vn.ntsc.di.scope.ServiceScope;
import com.vn.ntsc.services.StickerAndGiftDownloadService;
import com.vn.ntsc.services.addImageAlbum.UpLoadImageToAlbumService;
import com.vn.ntsc.services.uploadFileChat.PostStatusService;
import com.vn.ntsc.services.uploadFileChat.UploadFileService;

import dagger.Component;

/**
 * Created by nankai on 11/20/2017.
 */

@ServiceScope
@Component(dependencies = MediaComponent.class, modules = ServiceModule.class)
public interface ServiceMediaComponent {

    void inject(StickerAndGiftDownloadService stickerAndGiftDownloadService);

    void inject(UploadFileService uploadFileService);

    void inject(PostStatusService postStatusService);

    void inject(UpLoadImageToAlbumService upLoadImageToAlbumService);
}
