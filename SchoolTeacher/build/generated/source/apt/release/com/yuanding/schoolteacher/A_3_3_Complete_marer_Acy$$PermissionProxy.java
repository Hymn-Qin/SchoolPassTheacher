// Generated code. Do not modify!
package com.yuanding.schoolteacher;

import com.zhy.m.permission.*;

public class A_3_3_Complete_marer_Acy$$PermissionProxy implements PermissionProxy<A_3_3_Complete_marer_Acy> {
@Override
 public void grant(A_3_3_Complete_marer_Acy source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoSuccess();break;}  }
@Override
 public void denied(A_3_3_Complete_marer_Acy source , int requestCode) {
switch(requestCode) {case 2:source.requestPhotoFailed();break;}  }
@Override
 public void rationale(A_3_3_Complete_marer_Acy source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
