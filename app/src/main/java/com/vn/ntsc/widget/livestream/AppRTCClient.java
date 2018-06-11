package com.vn.ntsc.widget.livestream;

import org.webrtc.SessionDescription;

/**
 * Created by nankai on 12/20/2017.
 *
 *  AppRTCClient is the interface representing an AppRTC client.
 */

public interface AppRTCClient {

    /**
     * Asynchronously connect to an AppRTC room URL using supplied connection
     * parameters. Once connection is established onConnectedToRoom()
     * callback with room parameters is invoked.
     */
    void connectToRoom();

    /**
     * Disconnect from room.
     */
    void disconnectFromRoom();

    /**
     *  Send local offer SDP to the other participant.
     */
    void sendOfferSdp(final SessionDescription sdp);

    /**
     * Send local answer SDP to the other participant.
     */
    void sendAnswerSdp(final SessionDescription sdp);

    /**
     * send local reoffer to the other participant.
     */
    void sendReOffer();

    /**
     * Stop live stream and close peer.
     */
    void stopLiveStream();

    /**
     * displays on the timeline
     */
    void displaysOnTheTimeline();

    /**
     * send local check orientation to the other participant.
     *
     * @param rotate
     * @param streamID
     */
    void sendCheckOrientation(int rotate, String streamID);

    /**
     * send status for user view
     * @param status
     */
    void sendStatus(@MediaConnection.StreamStatus String status);

    /**
     *
     * @param webSocketRTCEvents
     */
    void setEvents(WebSocketRTCEvents webSocketRTCEvents);
}
