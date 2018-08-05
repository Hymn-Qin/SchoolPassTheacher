// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Side_Notice_Main_Sent_Notice_Files$$PermissionProxy implements PermissionProxy<B_Side_Notice_Main_Sent_Notice_Files> {
@Override
 public void grant(B_Side_Notice_Main_Sent_Notice_Files source , int requestCode) {
switch(requestCode) {case 11:source.requestSdcardSuccess();break;}  }
@Override
 public void denied(B_Side_Notice_Main_Sent_Notice_Files source , int requestCode) {
switch(requestCode) {case 11:source.requestSdcardFailed();break;}  }
@Override
 public void rationale(B_Side_Notice_Main_Sent_Notice_Files source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
