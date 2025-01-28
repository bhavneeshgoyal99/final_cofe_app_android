package com.cofe.solution.ui.device.add.list.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basic.G;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.SDBDeviceInfo;
import com.lib.sdk.struct.SDK_ChannelNameConfigAll;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;

import java.util.ArrayList;
import java.util.List;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.ChannelListAdapter;
import com.cofe.solution.ui.device.add.list.listener.ChannelListContract;
import com.cofe.solution.ui.device.add.list.presenter.ChannelListPresenter;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;

/**
 * 通道列表
 * List of channels
 * @author hws
 * @class
 * @time 2020/10/27 14:42
 */
public class ChannelListActivity extends DemoBaseActivity<ChannelListPresenter> implements
        ChannelListAdapter.OnItemChannelClickListener, ChannelListContract.IChannelListView {
    private RecyclerView rvChannelList;
    private ChannelListAdapter channelListAdapter;
    @Override
    public ChannelListPresenter getPresenter() {
        return new ChannelListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        loaderDialog.setMessage("THIS IS CHANNEL LIST");
        initView();
        initData();
    }

    private void initView() {
        rvChannelList = findViewById(R.id.rv_channel_list);
        rvChannelList.setLayoutManager(new LinearLayoutManager(this));
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.channel_list));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        titleBar.setBottomTip(getClass().getName());
    }

    private void initData() {
        channelListAdapter = new ChannelListAdapter(this);
        rvChannelList.setAdapter(channelListAdapter);
        XMDevInfo xmDevInfo = DevDataCenter.getInstance().getDevInfo(presenter.getDevId());
        if (xmDevInfo != null) {
            SDBDeviceInfo sdbDeviceInfo = xmDevInfo.getSdbDevInfo();
            if (sdbDeviceInfo != null) {
                SDK_ChannelNameConfigAll channelInfos = sdbDeviceInfo.getChannel();
                if (channelInfos != null) {
                    List<String> channelList = new ArrayList<>();
                    for (int i = 0 ; i < channelInfos.nChnCount; ++i) {
                        byte[] info = channelInfos.st_channelTitle[i];
                        String channelName = getString(R.string.channel) + ":" + i;
                        if (info != null) {
                            channelName += "(" + G.ToString(info) + ")";
                        }
                        /*if(channelInfos.nChnCount-1 == i ) {
                            turnToActivityLocal(DevMonitorActivity.class,new Object[][]{{"chnId",i},{"chnCount",channelListAdapter.getItemCount()}});
                        }*/
                        channelList.add(channelName);
                    }
                    channelListAdapter.setData(channelList);
                }
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        turnToActivityLocal(DevMonitorActivity.class,new Object[][]{{"chnId",position},{"chnCount",channelListAdapter.getItemCount()}});
    }


    @Override
    public boolean onLongItemClick(int position) {
        return false;
    }

    public void turnToActivityLocal(Class _class,Object[][] objects) {
        Intent intent = new Intent(this,_class);
        if (objects != null) {
            for (int i = 0; i < objects.length; ++i) {
                if (objects[i][1] instanceof Integer) {
                    intent.putExtra((String) objects[i][0],(Integer) objects[i][1]);
                }else if (objects[i][1] instanceof String) {
                    intent.putExtra((String) objects[i][0],(String) objects[i][1]);
                }else if (objects[i][1] instanceof Boolean) {
                    intent.putExtra((String) objects[i][0],(Boolean) objects[i][1]);
                }else if (objects[i][1] instanceof Double) {
                    intent.putExtra((String) objects[i][0],(Double) objects[i][1]);
                }else if (objects[i][1] instanceof Float) {
                    intent.putExtra((String) objects[i][0],(Float) objects[i][1]);
                }
            }
        }

        if (presenter != null && !StringUtils.isStringNULL(presenter.getDevId())) {
            intent.putExtra("devId",presenter.getDevId());
        }
        startActivity(intent);
        finish();
    }

}
