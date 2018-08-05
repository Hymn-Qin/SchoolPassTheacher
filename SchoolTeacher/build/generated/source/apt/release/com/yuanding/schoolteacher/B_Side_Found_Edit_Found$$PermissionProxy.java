// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class B_Side_Found_Edit_Found$$PermissionProxy implements PermissionProxy<B_Side_Found_Edit_Found> {
@Override
 public void grant(B_Side_Found_Edit_Found source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoSuccess();break;}  }
@Override
 public void denied(B_Side_Found_Edit_Found source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoFailed();break;}  }
@Override
 public void rationale(B_Side_Found_Edit_Found source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
