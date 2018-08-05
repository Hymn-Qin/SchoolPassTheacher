// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Side_Attence_Main_A3_Detail$$PermissionProxy implements PermissionProxy<B_Side_Attence_Main_A3_Detail> {
@Override
 public void grant(B_Side_Attence_Main_A3_Detail source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoSuccess();break;case 3:source.requestDingWeiSuccess();break;}  }
@Override
 public void denied(B_Side_Attence_Main_A3_Detail source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoFailed();break;case 3:source.requestDingWeiFailed();break;}  }
@Override
 public void rationale(B_Side_Attence_Main_A3_Detail source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
