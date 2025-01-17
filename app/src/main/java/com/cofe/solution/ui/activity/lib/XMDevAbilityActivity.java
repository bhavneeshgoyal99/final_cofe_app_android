package com.cofe.solution.ui.activity.lib;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.ui.activity.lib.XMBaseActivity;
import com.xm.activity.device.devset.ability.contract.XMDevAbilityContract;
import com.xm.activity.device.devset.ability.data.AbilityInfo;
import com.xm.activity.device.devset.ability.presenter.XMDevAbilityPresenter;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import demo.xm.com.libxmfunsdk.R;

/**
 * @author hws
 * @class 设备能力集
 * @time 2020/7/29 15:56
 */
public class XMDevAbilityActivity extends XMBaseActivity<XMDevAbilityPresenter> implements XMDevAbilityContract.IXMDevAbilityView {
    private XTitleBar xTitleBar;
    private RecyclerView rvDevAbility;
    private DevAbilityAdapter devAbilityAdapter;

    @Override
    public XMDevAbilityPresenter getPresenter() {
        return new XMDevAbilityPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.funsdk_xm_activity_dev_ability);
        initView();
        initData();
    }

    private void initView() {
        xTitleBar = findViewById(R.id.xb_dev_ability_title);
        xTitleBar.setLeftClick(this);
        rvDevAbility = findViewById(R.id.rv_dev_ability);
        rvDevAbility.setLayoutManager(new LinearLayoutManager(this));

        showProgress();
        presenter.updateDevAbility();
    }

    private void initData() {
        devAbilityAdapter = new DevAbilityAdapter();
        rvDevAbility.setAdapter(devAbilityAdapter);
    }

    @Override
    public void onUpdateDevAbilityResult(boolean isSuccess) {
        hideProgress();
        if (isSuccess) {
            devAbilityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    class DevAbilityAdapter extends RecyclerView.Adapter<DevAbilityAdapter.ViewHodler> {


        @NonNull
        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.funsdk_xm_adapter_dev_ability,null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
            AbilityInfo abilityInfo = presenter.getAbilityEnable(position);
            if (abilityInfo != null) {
                holder.lstDevAbility.setTitle(abilityInfo.getChildName());
                holder.lstDevAbility.setRightText(abilityInfo.isEnable() + "");
                holder.lstDevAbility.setTip(abilityInfo.getParentName());
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getAbilityCount();
        }

        class ViewHodler extends RecyclerView.ViewHolder {
            ListSelectItem lstDevAbility;
            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                lstDevAbility = itemView.findViewById(R.id.list_dev_ability);
            }
        }
    }
}
