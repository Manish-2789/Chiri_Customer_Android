package com.lotustechnologicalsolution.chiri.Interfaces;

import android.view.View;

public interface AdapterClickListenerCallback {

    void OnItemClick(int postion, Object modelObj, View view);

    void OnItemLongClick(int postion, Object modelObj, View view);
}
