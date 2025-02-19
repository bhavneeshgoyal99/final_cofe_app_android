package com.cofe.solution.ui.activity;

import com.manager.account.BaseAccountManager;

/**
 * @author hws
 * @name XMFunSDKDemo_Android
 * @class name：com.cofe.solution.ui.activity
 * @class describe
 * @time 2018-11-02 17:05
 * @change
 * @chang time
 * @class describe
 */
public class MainContract {
    public interface IMainView {
        void onUpdateView();
    }

    public interface IMainPresenter extends BaseAccountManager.OnAccountManagerListener{
        boolean isLoginByAccount();
    }
}
