// Generated code. Do not modify!
package io.dcloud;

import com.zhy.m.permission.*;

public class SDK_WebView$$PermissionProxy implements PermissionProxy<SDK_WebView> {
@Override
 public void grant(SDK_WebView source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneSuccess();break;}  }
@Override
 public void denied(SDK_WebView source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneFailed();break;}  }
@Override
 public void rationale(SDK_WebView source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
