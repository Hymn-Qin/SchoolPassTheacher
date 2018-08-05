// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Mess_Persion_Info$$PermissionProxy implements PermissionProxy<B_Mess_Persion_Info> {
@Override
 public void grant(B_Mess_Persion_Info source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneSuccess();break;}  }
@Override
 public void denied(B_Mess_Persion_Info source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneFailed();break;}  }
@Override
 public void rationale(B_Mess_Persion_Info source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
