package com.blackpointstraffic.helper;

import com.blackpointstraffic.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class DialogManager {
	public void showDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);

		if (status != null) {
			builder.setIcon((status) ? R.drawable.ic_success
					: R.drawable.ic_failure);
		}

		builder.setPositiveButton("Thử lại",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
