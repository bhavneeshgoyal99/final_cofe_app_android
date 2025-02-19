package com.cofe.solution.ui.device.config.cameralink.listener;

public class CameraLinkInitContract {
    public interface ICameraLinkInitView{
       void showWaitDlgShow(boolean show);
       void initFailed();
    }
    public interface ICameraLinkInitSetPresenter{}

}
