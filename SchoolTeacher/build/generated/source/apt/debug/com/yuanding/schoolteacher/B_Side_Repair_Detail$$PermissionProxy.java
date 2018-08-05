// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Side_Repair_Detail$$PermissionProxy implements PermissionProxy<B_Side_Repair_Detail> {
@Override
 public void grant(B_Side_Repair_Detail source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneSuccess();break;}  }
@Override
 public void denied(B_Side_Repair_Detail source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneFailed();break;}  }
@Override
 public void rationale(B_Side_Repair_Detail source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
