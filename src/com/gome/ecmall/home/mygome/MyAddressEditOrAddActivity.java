package com.gome.ecmall.home.mygome;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gome.ecmall.bean.ShoppingCart;
import com.gome.ecmall.bean.ShoppingCart.Division;
import com.gome.ecmall.bean.ShoppingCart.ShoppingCart_Recently_address;
import com.gome.ecmall.custom.LoadingDialog;
import com.gome.ecmall.shopping.ShoppingCartOrderConsInfoActivity;
import com.gome.ecmall.util.CommonUtility;
import com.gome.ecmall.util.Constants;
import com.gome.ecmall.util.NetUtility;
import com.gome.eshopnew.R;

/**
 * @author qiudongchao 添加--编辑--地址
 */
public class MyAddressEditOrAddActivity extends Activity implements OnClickListener {

    private static final String Tag = "MyAddressEditOrAddActivity";
    // 省市区标识
    private static final int province_type = 1;
    private static final int city_type = 2;
    private static final int discont_type = 3;
    private Button common_title_btn_back, common_title_btn_right, shopping_province_data, shopping_city_data,
            shopping_discont_data;
    private TextView tvTitle;
    private EditText shopping_goods_cons_name_data, shopping_goods_cons_phone_data,
            shopping_goods_cons_detailaddress_data, shopping_goods_cons_email_data, shopping_goods_cons_zope_data;
    private String provinceId;
    private String cityId;
    private String districtId;
    // 省列表
    private List<Division> provinceList;
    private String[] provinceDataArray;
    private int provinceIndex;
    private List<Division> cityList;
    private String[] cityDataArray;
    private int cityIndex;
    private List<Division> discontList;
    private String[] discontDataArray;
    private int discontIndex;
    private String cmd;
    private static final String EDIT = "edit";
    private static final String CMD = "cmd";
    private ShoppingCart_Recently_address currentAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_consinfo_add);
        initTitleButton();
        cmd = getIntent().getStringExtra(CMD);
        if (cmd.equalsIgnoreCase(EDIT)) {
            currentAddress = (ShoppingCart_Recently_address) getIntent().getSerializableExtra("consInfo_address");
        }
        initViewData(currentAddress);
    }

    /**
     * 初始化页面控件
     */
    private void initTitleButton() {
        common_title_btn_back = (Button) findViewById(R.id.common_title_btn_back);
        common_title_btn_back.setVisibility(View.VISIBLE);
        common_title_btn_back.setText(R.string.back);
        common_title_btn_back.setBackgroundResource(R.drawable.common_top_title_btn_bg_selector);
        common_title_btn_back.setOnClickListener(this);
        common_title_btn_right = (Button) findViewById(R.id.common_title_btn_right);
        common_title_btn_right.setVisibility(View.VISIBLE);
        common_title_btn_right.setText(R.string.shopping_goods_order_consinfo_save_newaddress);
        common_title_btn_right.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.common_title_tv_text);
        tvTitle.setText(R.string.shopping_goods_order_consinfo_add_newaddress);
        shopping_goods_cons_name_data = (EditText) findViewById(R.id.shopping_goods_cons_name_data);
        shopping_goods_cons_phone_data = (EditText) findViewById(R.id.shopping_goods_cons_phone_data);
        shopping_goods_cons_detailaddress_data = (EditText) findViewById(R.id.shopping_goods_cons_detailaddress_data);
        shopping_goods_cons_email_data = (EditText) findViewById(R.id.shopping_goods_cons_email_data);
        shopping_goods_cons_zope_data = (EditText) findViewById(R.id.shopping_goods_cons_zope_data);
        shopping_province_data = (Button) findViewById(R.id.shopping_province_data);
        shopping_province_data.setOnClickListener(this);
        shopping_city_data = (Button) findViewById(R.id.shopping_city_data);
        shopping_city_data.setOnClickListener(this);
        shopping_discont_data = (Button) findViewById(R.id.shopping_discont_data);
        shopping_discont_data.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.common_title_btn_right:
            saveOrModifyAddress();
            break;
        case R.id.shopping_province_data: {
            if (provinceList == null) {
                getDivision(province_type, "");
            } else {
                for (int i = 0 , size = provinceList.size(); i < size; i++) {
                    if (!TextUtils.isEmpty(provinceId) && provinceId.equals(provinceList.get(i).getDivisionCode())) {
                        provinceIndex = i;
                        break;
                    }
                }
                CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                        getString(R.string.shopping_goods_order_consinfo_province), provinceDataArray, provinceIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int checkIndex) {
                                dialog.dismiss();
                                provinceIndex = checkIndex;
                                if (provinceDataArray != null && provinceDataArray.length != 0) {
                                    provinceId = (provinceList.get(provinceIndex)).getDivisionCode();
                                    shopping_province_data.setText(provinceDataArray[provinceIndex]);
                                    shopping_city_data.setText(R.string.shopping_goods_order_consinfo_city);
                                    shopping_discont_data.setText(R.string.shopping_goods_order_consinfo_discont);
                                    clearCityData();
                                }
                                // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                // provinceDataArray[provinceIndex],
                                // Toast.LENGTH_SHORT).show();
                            }
                        }, null, null, null, null);
            }
        }
            break;
        case R.id.shopping_city_data: {
            if (cityList == null) {
                if (!TextUtils.isEmpty(provinceId)) {
                    if (provinceList != null && provinceList.size() != 0) {
                        getDivision(city_type, ((Division) provinceList.get(provinceIndex)).getDivisionCode());
                    } else {
                        getDivision(city_type, provinceId);
                    }
                }
            } else {
                for (int i = 0 , size = cityList.size(); i < size; i++) {
                    if (!TextUtils.isEmpty(cityId) && cityId.equals(cityList.get(i).getDivisionCode())) {
                        cityIndex = i;
                        break;
                    }
                }
                CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                        getString(R.string.shopping_goods_order_consinfo_city), cityDataArray, cityIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int checkIndex) {
                                dialog.dismiss();
                                cityIndex = checkIndex;
                                if (cityDataArray != null && cityDataArray.length != 0) {
                                    cityId = (cityList.get(checkIndex)).getDivisionCode();
                                    shopping_city_data.setText(cityDataArray[cityIndex]);
                                    shopping_discont_data.setText(R.string.shopping_goods_order_consinfo_discont);
                                    clearDiscontData();
                                }
                                // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                // cityDataArray[cityIndex],
                                // Toast.LENGTH_SHORT).show();
                            }
                        }, null, null, null, null);
            }

        }
            break;
        case R.id.shopping_discont_data: {
            if (discontList == null) {
                if (!TextUtils.isEmpty(cityId)) {
                    if (cityList != null && cityList.size() != 0) {
                        getDivision(discont_type, ((Division) cityList.get(cityIndex)).getDivisionCode());
                    } else {
                        getDivision(discont_type, cityId);
                    }
                }
            } else {
                for (int i = 0 , size = discontList.size(); i < size; i++) {
                    if (!TextUtils.isEmpty(districtId) && districtId.equals(discontList.get(i).getDivisionCode())) {
                        discontIndex = i;
                        break;
                    }
                }

                CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                        getString(R.string.shopping_goods_order_consinfo_discont), discontDataArray, discontIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int checkIndex) {
                                dialog.dismiss();
                                discontIndex = checkIndex;
                                districtId = (discontList.get(discontIndex)).getDivisionCode();
                                if (discontDataArray != null && discontDataArray.length != 0) {
                                    shopping_discont_data.setText(discontDataArray[discontIndex]);
                                }
                                // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                // discontDataArray[discontIndex],
                                // Toast.LENGTH_SHORT).show();
                            }
                        }, null, null, null, null);
            }
        }
            break;
        case R.id.common_title_btn_back:
            finish();
            break;
        default:
            break;
        }
    }

    /**
     * 初始化页面数据
     * 
     * @param currentAddress
     */
    private void initViewData(ShoppingCart_Recently_address currentAddress) {
        if (cmd.equalsIgnoreCase(EDIT)) {
            // 保存
            tvTitle.setText(getString(R.string.mygome_address_edit));
            // 页面数据绑定
            shopping_goods_cons_name_data.setText(currentAddress.getConsignee());
            String phone = currentAddress.getPhone();
            String mobile = currentAddress.getMobile();
            String showPhoneMoble = "";
            if (!TextUtils.isEmpty(phone))
                showPhoneMoble = phone;
            else
                showPhoneMoble = mobile;
            provinceId = currentAddress.getProvinceId();
            cityId = currentAddress.getCityId();
            districtId = currentAddress.getDistrictId();
            shopping_province_data
                    .setText(TextUtils.isEmpty(currentAddress.getProvinceName()) ? getString(R.string.shopping_goods_order_consinfo_province)
                            : currentAddress.getProvinceName());
            shopping_city_data
                    .setText(TextUtils.isEmpty(currentAddress.getCityName()) ? getString(R.string.shopping_goods_order_consinfo_city)
                            : currentAddress.getCityName());
            shopping_discont_data
                    .setText(TextUtils.isEmpty(currentAddress.getDistrictName()) ? getString(R.string.shopping_goods_order_consinfo_discont)
                            : currentAddress.getDistrictName());
            shopping_goods_cons_phone_data.setText(showPhoneMoble);
            shopping_goods_cons_detailaddress_data.setText(currentAddress.getAddress());
            shopping_goods_cons_email_data.setText(currentAddress.getEmail());
            shopping_goods_cons_zope_data.setText(currentAddress.getZipCode());
        } else {
            // 添加 TODO
            tvTitle.setText(getString(R.string.mygome_address_add));
        }
    }

    /**
     * 保存地址--封装实体
     */
    private void saveOrModifyAddress() {
        String cons_name_data = shopping_goods_cons_name_data.getText().toString();
        if (ShoppingCartOrderConsInfoActivity.isEmpty(MyAddressEditOrAddActivity.this, cons_name_data,
                getString(R.string.shopping_goods_order_consinfo_name_null))) {
            return;
        }
        if (!ShoppingCartOrderConsInfoActivity.isConsNameYes(cons_name_data)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.shopping_goods_order_consinfo_name_ren));
            return;
        }
        String cons_phone_data = shopping_goods_cons_phone_data.getText().toString();
        if (ShoppingCartOrderConsInfoActivity.isEmpty(MyAddressEditOrAddActivity.this, cons_phone_data,
                getString(R.string.shopping_goods_order_consinfo_phone_null))) {
            return;
        } else if (!ShoppingCartOrderConsInfoActivity.isPhone(cons_phone_data)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.shopping_goods_order_consinfo_phone_ren));
            return;
        }
        if (TextUtils.isEmpty(provinceId) || TextUtils.isEmpty(cityId) || TextUtils.isEmpty(districtId)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.shopping_goods_order_consinfo_seleaddress_null));
            return;
        }
        String cons_detailaddress_data = shopping_goods_cons_detailaddress_data.getText().toString();
        if (ShoppingCartOrderConsInfoActivity.isEmpty(MyAddressEditOrAddActivity.this, cons_detailaddress_data,
                getString(R.string.shopping_goods_order_consinfo_address_null))) {
            return;
        }
        String email_data = shopping_goods_cons_email_data.getText().toString();
        if (!TextUtils.isEmpty(email_data) && !ShoppingCartOrderConsInfoActivity.isEmail(email_data)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.shopping_goods_order_consinfo_email_ren));
            return;
        }
        String zope_data = shopping_goods_cons_zope_data.getText().toString();
        if (!TextUtils.isEmpty(zope_data) && zope_data.length() != 6 && zope_data.length() != 0) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.shopping_goods_order_consinfo_zope_ren));
            return;
        }
        ShoppingCart_Recently_address consInfoAddress = new ShoppingCart_Recently_address();
        consInfoAddress.setConsignee(cons_name_data);
        consInfoAddress.setPhone(cons_phone_data);
        consInfoAddress.setProvinceId(provinceId);
        consInfoAddress.setCityId(cityId);
        consInfoAddress.setDistrictId(districtId);
        consInfoAddress.setAddress(cons_detailaddress_data);
        consInfoAddress.setEmail(TextUtils.isEmpty(email_data) ? "" : email_data);
        consInfoAddress.setZipCode(TextUtils.isEmpty(zope_data) ? "" : zope_data);
        if (cmd.equalsIgnoreCase(EDIT) && currentAddress != null) {
            String id = currentAddress.getId();
            consInfoAddress.setId(TextUtils.isEmpty(id) ? "" : id);
        } else {
            consInfoAddress.setId("");
        }
        saveOrModifyAddressData(consInfoAddress);
    }

    /**
     * 保存地址
     * 
     * @param consInfoAddress
     */
    private void saveOrModifyAddressData(final ShoppingCart_Recently_address consInfoAddress) {
        if (!NetUtility.isNetworkAvailable(MyAddressEditOrAddActivity.this)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.can_not_conntect_network_please_check_network_settings));
            return;
        }
        new AsyncTask<Object, Void, Object[]>() {
            LoadingDialog progressDialog;

            protected void onPreExecute() {
                progressDialog = CommonUtility.showLoadingDialog(MyAddressEditOrAddActivity.this,
                        getString(R.string.loading), true, new OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                                cancel(true);
                            }
                        });
            };

            protected Object[] doInBackground(Object... params) {
                String body = MyAddressService.buildRequestMyGome_Address_add(consInfoAddress);
                String result = NetUtility.NO_CONN;
                result = NetUtility.sendHttpRequestByPost(Constants.URL_MYGOME_SAVEADDRESS, body);
                if (NetUtility.NO_CONN.equals(result)) {
                    return null;
                }
                return MyAddressService.paserResponseMyGome_Address_add(result);
            };

            protected void onPostExecute(Object[] objs) {
                progressDialog.dismiss();
                if (isCancelled()) {
                    return;
                }
                if (objs == null || (Boolean) objs[0] == null) {
                    CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                            getString(R.string.data_load_fail_exception));
                    return;
                }
                if ((Boolean) objs[0]) {
                    // 修改成功
                    /*
                     * CommonUtility.showAlertDialog(MyAddressEditOrAddActivity.this, "提示", "保存成功!", "确定", new
                     * DialogInterface.OnClickListener(){
                     * 
                     * @Override public void onClick(DialogInterface dialog, int which) { dialog.cancel(); setResult(0);
                     * MyAddressEditOrAddActivity.this.finish(); }});
                     */
                    CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "", "保存成功!");
                    setResult(0);
                    MyAddressEditOrAddActivity.this.finish();

                } else if (!(Boolean) objs[0] && objs.length == 2) {
                    CommonUtility.showAlertDialog(MyAddressEditOrAddActivity.this, "", (String) objs[1],
                            getString(R.string.confirm), rightListener);
                }
            };
        }.execute();
    }

    private android.content.DialogInterface.OnClickListener rightListener = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            dialog.cancel();
        }

    };

    /**
     * 获取地区列表
     */
    private void getDivision(final int divisionLevel, final String parentDivisionCode) {
        if (!NetUtility.isNetworkAvailable(MyAddressEditOrAddActivity.this)) {
            CommonUtility.showMiddleToast(MyAddressEditOrAddActivity.this, "",
                    getString(R.string.can_not_conntect_network_please_check_network_settings));
            return;
        }
        new AsyncTask<Object, Void, List<Division>>() {
            LoadingDialog progressDialog;

            protected void onPreExecute() {
                progressDialog = CommonUtility.showLoadingDialog(MyAddressEditOrAddActivity.this,
                        getString(R.string.loading), true, new OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                                cancel(true);
                            }
                        });
            };

            protected List<Division> doInBackground(Object... params) {
                String body = ShoppingCart.create_AddressInfo(divisionLevel, parentDivisionCode);
                String result = NetUtility.sendHttpRequestByPost(Constants.URL_ADDRESSAREA, body);
                return ShoppingCart.getDivisionList(result);
            };

            protected void onPostExecute(List<Division> divisionList) {
                progressDialog.dismiss();
                if (isCancelled()) {
                    return;
                }
                if (divisionList == null) {
                    CommonUtility.showToast(MyAddressEditOrAddActivity.this,
                            getString(R.string.data_load_fail_exception));
                    return;
                }
                switch (divisionLevel) {
                case province_type: {
                    provinceList = divisionList;
                    if (provinceList != null) {
                        provinceDataArray = new String[provinceList.size()];
                        for (int i = 0, size = provinceList.size(); i < size; i++) {
                            Division division = provinceList.get(i);
                            if (division != null) {
                                provinceDataArray[i] = division.getDivisionName();
                            }
                            if (!TextUtils.isEmpty(provinceId) && provinceId.equals(division.getDivisionCode())) {
                                provinceIndex = i;
                            }
                        }
                    }
                    CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                            getString(R.string.shopping_goods_order_consinfo_province), provinceDataArray,
                            provinceIndex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int checkIndex) {
                                    dialog.dismiss();
                                    provinceIndex = checkIndex;
                                    if (provinceDataArray != null && provinceDataArray.length != 0) {
                                        provinceId = (provinceList.get(provinceIndex)).getDivisionCode();
                                        shopping_province_data.setText(provinceDataArray[provinceIndex]);
                                        shopping_city_data.setText(R.string.shopping_goods_order_consinfo_city);
                                        shopping_discont_data.setText(R.string.shopping_goods_order_consinfo_discont);
                                        clearCityData();
                                    }
                                    // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                    // provinceDataArray[provinceIndex],
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }, null, null, null, null);

                }
                    break;
                case city_type: {
                    cityList = divisionList;
                    if (cityList != null) {
                        cityDataArray = new String[cityList.size()];
                        for (int i = 0, size = cityList.size(); i < size; i++) {
                            Division division = cityList.get(i);
                            if (division != null) {
                                cityDataArray[i] = division.getDivisionName();
                            }
                            if (!TextUtils.isEmpty(cityId) && cityId.equals(division.getDivisionCode())) {
                                cityIndex = i;
                            }
                        }

                    }
                    CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                            getString(R.string.shopping_goods_order_consinfo_city), cityDataArray, cityIndex,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int checkIndex) {
                                    dialog.dismiss();
                                    cityIndex = checkIndex;
                                    if (cityDataArray != null && cityDataArray.length != 0) {
                                        cityId = (cityList.get(checkIndex)).getDivisionCode();
                                        shopping_city_data.setText(cityDataArray[cityIndex]);
                                        shopping_discont_data.setText(R.string.shopping_goods_order_consinfo_discont);
                                        clearDiscontData();
                                    }
                                    // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                    // cityDataArray[cityIndex],
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }, null, null, null, null);
                }

                    break;
                case discont_type: {
                    discontList = divisionList;
                    if (discontList != null) {
                        discontDataArray = new String[discontList.size()];
                        for (int i = 0, size = discontList.size(); i < size; i++) {
                            Division division = discontList.get(i);
                            if (division != null) {
                                discontDataArray[i] = division.getDivisionName();
                            }
                            if (!TextUtils.isEmpty(districtId) && districtId.equals(division.getDivisionCode())) {
                                discontIndex = i;
                            }
                        }
                    }
                    CommonUtility.showSingleChioceDialog(MyAddressEditOrAddActivity.this,
                            getString(R.string.shopping_goods_order_consinfo_discont), discontDataArray, discontIndex,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int checkIndex) {
                                    dialog.dismiss();
                                    discontIndex = checkIndex;
                                    districtId = (discontList.get(discontIndex)).getDivisionCode();
                                    if (discontDataArray != null && discontDataArray.length != 0) {
                                        shopping_discont_data.setText(discontDataArray[discontIndex]);
                                    }
                                    // Toast.makeText(ShoppingCartOrderConsInfoActivity.this,
                                    // discontDataArray[discontIndex],
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }, null, null, null, null);

                }
                    break;
                default:
                    break;
                }
                // initViewData(shoppingCart_consInfo_address);
            };
        }.execute();
    }

    /**
     * 清空城市
     */
    private void clearCityData() {
        cityId = "";
        districtId = "";
        cityList = null;
        discontList = null;
        cityDataArray = null;
        discontDataArray = null;
        cityIndex = 0;
        discontIndex = 0;
    }

    /**
     * 清空地区
     */
    private void clearDiscontData() {
        districtId = "";
        discontList = null;
        discontDataArray = null;
        discontDataArray = null;
        discontIndex = 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
