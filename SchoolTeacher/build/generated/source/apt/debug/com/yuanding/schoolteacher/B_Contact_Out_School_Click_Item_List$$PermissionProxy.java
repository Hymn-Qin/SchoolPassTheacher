// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Contact_Out_School_Click_Item_List$$PermissionProxy implements PermissionProxy<B_Contact_Out_School_Click_Item_List> {
@Override
 public void grant(B_Contact_Out_School_Click_Item_List source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneSuccess();break;}  }
@Override
 public void denied(B_Contact_Out_School_Click_Item_List source , int requestCode) {
switch(requestCode) {case 4:source.requestCallPhoneFailed();break;}  }
@Override
 public void rationale(B_Contact_Out_School_Click_Item_List source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
