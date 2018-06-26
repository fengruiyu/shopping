package com.example.dell.shopping;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCartAdaper extends BaseExpandableListAdapter {
    private static final String TAG = "MyCartAdaper";
    private List<JavaBean.DataBean> sellerData;
    private Context context;

    public MyCartAdaper(List<JavaBean.DataBean> sellerData, Context context) {
        this.sellerData = sellerData;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return sellerData == null ? 0 : sellerData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sellerData.get(groupPosition).getList() == null ? 0 : sellerData.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder parentViewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_cart_parent, null);
            parentViewHolder = new ParentViewHolder(convertView);
            convertView.setTag(parentViewHolder);
        }else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }
        //商家名字
        parentViewHolder.sellerNameTv.setText(sellerData.get(groupPosition).getSellerName());
        //根据商品确定商家得checkbox是否被选中
        boolean currentSellerAllProductSelected = isCurrentSellerAllProductSelected(groupPosition);
        parentViewHolder.sellerCb.setChecked(currentSellerAllProductSelected);
        parentViewHolder.sellerCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCartListChangerListener!=null){
                    onCartListChangerListener.onSellerCheckBoxChange(groupPosition);
                }
            }
        });
        return convertView;
    }
    //当商家得checkbox被点击得时候调用，设置当前商家得所有商品得状态\
    //改变所有子复选框的状态
    public void Changeeverything(int groupPosition,boolean ischange){

        JavaBean.DataBean dataBean = sellerData.get(groupPosition);
        List<JavaBean.DataBean.ListBean> list = dataBean.getList();
        for (int i = 0; i < list.size(); i++) {
            JavaBean.DataBean.ListBean listBean = list.get(i);
            listBean.setSelected(ischange ? 1 : 0);
        }
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        List<JavaBean.DataBean.ListBean> list = sellerData.get(groupPosition).getList();
        //商品
        JavaBean.DataBean.ListBean listBean = list.get(childPosition);

        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_cart_child, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        //商品名字
        childViewHolder.productTitleNameTv.setText(listBean.getTitle());
        //商品单价
        childViewHolder.productPriceTv.setText(listBean.getPrice()+"");
        //图片
        String[] split = listBean.getImages().split("\\|");
        Glide.with(context).load(split[0]).into(childViewHolder.childIv);
        //商品得checkBox状态
        childViewHolder.childCb.setChecked(listBean.getSelected() == 1);
        childViewHolder.childCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击商品得checkBox
                if (onCartListChangerListener!=null){
                    onCartListChangerListener.onProductCheckBoxChange(groupPosition,childPosition);
                }
            }
        });
        childViewHolder.adderSubtractor.setNumber(listBean.getNum());
        childViewHolder.adderSubtractor.setOnNumberChangeListener(new MyAddSubView.OnNumberChangeListener() {
            @Override
            public void numberChange(int num) {
                if (onCartListChangerListener !=null){
                    onCartListChangerListener.onProductNumberChange(groupPosition,childPosition,num);
                }
            }
        });
        return convertView;
    }
    //当商品得checkbox被点击得时候调用，改变当前商品状态
    public void changeCurrentProductStatus(int groupPosition,int childPosition){
        JavaBean.DataBean dataBean = sellerData.get(groupPosition);
        JavaBean.DataBean.ListBean listBean = dataBean.getList().get(childPosition);
        listBean.setSelected(listBean.getSelected()==0 ? 1 :0);
    };
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    //根据商品确定商家得checkbox是否被选中
    public boolean isCurrentSellerAllProductSelected(int groupPosition) {
        List<JavaBean.DataBean.ListBean> list = sellerData.get(groupPosition).getList();
        for(JavaBean.DataBean.ListBean listBean : list){
            //只要有一个未选中，商家就直接未选中
            if (listBean.getSelected() == 0){
                return false;
            }
        }
        return true;
    }
    //所有商品是否被选中
    public boolean isAllProductsSelected() {
        for (int i = 0; i < sellerData.size(); i++) {
            List<JavaBean.DataBean.ListBean> list = sellerData.get(i).getList();
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getSelected()==0){
                    return false;
                }
            }
        }
        return true;
    }
    //计算总价
    public float calculatetotalprice() {
        float number = 0;
        for (int i = 0; i < sellerData.size(); i++) {
            List<JavaBean.DataBean.ListBean> list = sellerData.get(i).getList();
            for (int j = 0; j < list.size(); j++) {
                JavaBean.DataBean.ListBean listBean = list.get(j);
                if (listBean.getSelected()==1){
                    float price = listBean.getPrice();
                    int num = listBean.getNum();
                    number += price*num;
                }
            }
        }
        return number;
    }

    public int calculatetotalNumber() {
        int number = 0;
        for (int i = 0; i < sellerData.size(); i++) {
            List<JavaBean.DataBean.ListBean> list = sellerData.get(i).getList();
            for (int j = 0; j < list.size(); j++) {
                JavaBean.DataBean.ListBean listBean = list.get(j);
                if (listBean.getSelected()==1){
                    int num = listBean.getNum();
                    number += num;
                }
            }
        }
        return number;
    }
    //当加减器被点击得时候调用，改变当前商品得数量
    public void changeCurrentProductNumber(int groupPosition, int childPosition, int number) {
        JavaBean.DataBean dataBean = sellerData.get(groupPosition);
        JavaBean.DataBean.ListBean listBean = dataBean.getList().get(childPosition);
        listBean.setNum(number);
    }

    public void ChangeallProductsSelected(boolean b) {
        for (int i = 0; i < sellerData.size(); i++) {
            List<JavaBean.DataBean.ListBean> list = sellerData.get(i).getList();
            for (int j = 0; j < list.size(); j++) {
                list.get(j).setSelected(b ? 1 : 0);
            }
        }
    }

    static class ParentViewHolder {
        @BindView(R.id.seller_cb)
        CheckBox sellerCb;
        @BindView(R.id.seller_name_tv)
        TextView sellerNameTv;

        ParentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.child_cb)
        CheckBox childCb;
        @BindView(R.id.child_iv)
        ImageView childIv;
        @BindView(R.id.product_title_name_tv)
        TextView productTitleNameTv;
        @BindView(R.id.product_price_tv)
        TextView productPriceTv;
        @BindView(R.id.adder_subtractor)
        MyAddSubView adderSubtractor;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    //onCartListChangeListener接口回调
    OnCartListChangerListener onCartListChangerListener;

    public void setOnCartListChangerListener(OnCartListChangerListener onCartListChangerListener) {
        this.onCartListChangerListener = onCartListChangerListener;
    }

    public interface OnCartListChangerListener{
        void onSellerCheckBoxChange(int groupPosition);
        void onProductCheckBoxChange(int groupPosition,int childPosition);
        void onProductNumberChange(int groupPosition,int childPosition,int number);

    }
}
