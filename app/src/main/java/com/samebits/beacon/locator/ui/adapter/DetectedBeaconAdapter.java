/*
 *
 *  Copyright (c) 2015 SameBits UG. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.samebits.beacon.locator.ui.adapter;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samebits.beacon.locator.R;
import com.samebits.beacon.locator.databinding.ItemDetectedBeaconBinding;
import com.samebits.beacon.locator.model.DetectedBeacon;
import com.samebits.beacon.locator.ui.fragment.BeaconFragment;
import com.samebits.beacon.locator.viewModel.DetectedBeaconViewModel;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by vitas on 09/12/2015.
 */
public class DetectedBeaconAdapter extends BeaconAdapter<DetectedBeaconAdapter.BindingHolder> {
    String json_string;
    DecimalFormat df;
    private String json_response;
    public DetectedBeaconAdapter(BeaconFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDetectedBeaconBinding beaconBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_detected_beacon,
                parent,
                false);
        return new BindingHolder(beaconBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        ItemDetectedBeaconBinding beaconBinding = holder.binding;
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onBeaconLongClickListener != null) {
                    onBeaconLongClickListener.onBeaconLongClick(position);
                }
                return false;
            }
        });
        beaconBinding.setViewModel(new DetectedBeaconViewModel(mFragment, (DetectedBeacon) getItem(position)));
    }


    public void insertBeacons(Collection<Beacon> beacons) {
        df = new DecimalFormat("0.####");
        json_string="";
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("user", "default");

            try {
                // In this case we need a json array to hold the java list
                    JSONArray jsonArr = new JSONArray();
                    for (Beacon beacon : beacons) {
                        Identifier namespaceId = beacon.getId1();
                        Identifier instanceId = beacon.getId2();
                        JSONObject jsonBeacon=new JSONObject();
                        jsonBeacon.put("uuid", String.valueOf(namespaceId).substring(2) + String.valueOf(instanceId).substring(2));
                        jsonBeacon.put("distance", df.format(beacon.getDistance()));
                        jsonArr.put(jsonBeacon);
                    }
                jsonObj.put("beacons", jsonArr);
                json_string=jsonObj.toString();
                addJSON();
                }
                catch (Exception d){

                }
        }
        catch (JSONException e){}


        Iterator<Beacon> iterator = beacons.iterator();
        while (iterator.hasNext()) {
            DetectedBeacon dBeacon = new DetectedBeacon(iterator.next());
            dBeacon.setTimeLastSeen(System.currentTimeMillis());
            this.mBeacons.put(dBeacon.getId(), dBeacon);
        }
        notifyDataSetChanged();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemDetectedBeaconBinding binding;

        public BindingHolder(ItemDetectedBeaconBinding binding) {
            super(binding.contentView);
            this.binding = binding;
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            binding.contentView.setOnLongClickListener(listener);
        }
    }

    private void addJSON(){
        final String url="http://153.97.4.191:8082/updateBeacon";
        class AddBeacon extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                json_response=s;
                //getBeaconResponse();
            }

            @Override
            protected String doInBackground(Void... v) {
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequestJSON(url, json_string);
                return res;

            }
        }

        AddBeacon anm = new AddBeacon();
        anm.execute();
    }

}
