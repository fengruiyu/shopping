package com.example.dell.shopping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.mian_overall_cb)
    CheckBox mianOverallCb;
    @BindView(R.id.mian_overall_total_tv)
    TextView mianOverallTotalTv;
    @BindView(R.id.btn_cart_pay)
    Button btnCartPay;
    private MyCartAdaper myCartAdaper;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        expandableListView = findViewById(R.id.main_exlist);
        initnews();
        initbtnCartPay();
    }

    private void initbtnCartPay() {
        mianOverallCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allProductsSelected = myCartAdaper.isAllProductsSelected();
                myCartAdaper.ChangeallProductsSelected(!allProductsSelected);
                myCartAdaper.notifyDataSetChanged();
                //刷新checkbox状态和总价和总数量
                refreshSelectedAndTotalPriceAndTotalNumber();
            }
        });
    }

    private void initnews() {
        String urls = "https://www.zhaoapi.cn/product/getCarts";
        Map<String, String> map = new HashMap<>();
        map.put("uid", "71");
        OkUtils okUtils = OkUtils.getokClient();
        okUtils.doPost(urls, map, new OkUtils.IOkutils() {
            @Override
            public void onFailure(IOException e) {
            }

            @Override
            public void onResponse(String json) {
                Gson gson = new Gson();
                JavaBean javaBean = gson.fromJson(json, JavaBean.class);
                List<JavaBean.DataBean> data = javaBean.getData();
                Log.d(TAG, data + "");
                myCartAdaper = new MyCartAdaper(data, MainActivity.this);
                expandableListView.setAdapter(myCartAdaper);
                //展开二级列表
                for (int i = 0; i < data.size(); i++) {
                    expandableListView.expandGroup(i);
                }
                myCartAdaper.setOnCartListChangerListener(new MyCartAdaper.OnCartListChangerListener() {
                    @Override
                    public void onSellerCheckBoxChange(int groupPosition) {
                        boolean currentSellerAllProductSelected = myCartAdaper.isCurrentSellerAllProductSelected(groupPosition);

                        myCartAdaper.Changeeverything(groupPosition, !currentSellerAllProductSelected);
                        myCartAdaper.notifyDataSetChanged();
                        //刷新checkbox状态和总价和总数量
                        refreshSelectedAndTotalPriceAndTotalNumber();
                    }

                    @Override
                    public void onProductCheckBoxChange(int groupPosition, int childPosition) {
                        Log.d(TAG, "erre" + groupPosition);
                        myCartAdaper.changeCurrentProductStatus(groupPosition, childPosition);
                        myCartAdaper.notifyDataSetChanged();
                        //刷新checkbox状态和总价和总数量
                        refreshSelectedAndTotalPriceAndTotalNumber();
                    }

                    @Override
                    public void onProductNumberChange(int groupPosition, int childPosition, int number) {
                        myCartAdaper.changeCurrentProductNumber(groupPosition, childPosition, number);
                        myCartAdaper.notifyDataSetChanged();
                        //刷新checkbox状态和总价和总数量
                        refreshSelectedAndTotalPriceAndTotalNumber();
                    }
                });
                //刷新checkbox状态和总价和总数量
                refreshSelectedAndTotalPriceAndTotalNumber();
            }


        });
    }

    //刷新checkbox状态和总价和总数量
    private void refreshSelectedAndTotalPriceAndTotalNumber() {
        //去判断是否所有得商品都被选中
        boolean allProductsSelected = myCartAdaper.isAllProductsSelected();
        //设置给全选checkBox
        mianOverallCb.setChecked(allProductsSelected);
        //计算总价
        float totalPrice = myCartAdaper.calculatetotalprice();
        mianOverallTotalTv.setText("总价" + totalPrice);
        //计算数量
        int totalumber = myCartAdaper.calculatetotalNumber();
        btnCartPay.setText("去结算(" + totalumber + ")");

    }

    @OnClick(R.id.mian_overall_cb)
    public void onViewClicked() {
    }
}
