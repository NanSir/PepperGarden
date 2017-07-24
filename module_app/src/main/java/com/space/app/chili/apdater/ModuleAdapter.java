package com.space.app.chili.apdater;

import android.app.Activity;
import android.widget.ImageView;


import com.sir.app.base.BaseRecyclerAdapter;
import com.sir.app.base.help.ViewHolder;
import com.space.app.chili.R;

/**
 * 模型列表
 * Created by zhuyinan on 2017/6/8.
 */

public class ModuleAdapter extends BaseRecyclerAdapter<Integer> {

    public ModuleAdapter(Activity mContext) {
        super(mContext);
    }


    @Override
    public void onBindHolder(ViewHolder holder, int position) {
        ImageView ivModule = holder.getView(R.id.iv_module);
        ivModule.setBackgroundResource(getItem(position));
    }

    @Override
    public int bindLayout() {
        return R.layout.item_module;
    }

}
