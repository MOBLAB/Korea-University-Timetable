package com.proinlab.kut.functions;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.proinlab.kut.R;

public class ListViewCustomAdapter extends BaseAdapter {
	Context maincon;
	LayoutInflater Inflater;
	ArrayList<String[]> arSrc;
	int layout;

	/**
	 * 커스텀 리스트뷰 아답터를 생성한다
	 * 
	 * @param context
	 * @param alayout
	 * @param aarSrc
	 *            검색된 모든 강의 정보
	 */
	public ListViewCustomAdapter(Context context, int alayout,
			ArrayList<String[]> aarSrc) {
		maincon = context;
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = alayout;
	}

	public int getCount() {
		return arSrc.size();
	}

	public String getItem(int position) {
		return arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_12_LINK];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}

		for (int i = 0; i < arSrc.get(position).length; i++) {
			if (arSrc.get(position)[i].equals("none"))
				arSrc.get(position)[i] = "";
		}

		TextView Title = (TextView) convertView
				.findViewById(R.id.timetable_customlist_lecname);
		String Titles = arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME];
		if (Titles.indexOf(":") != -1)
			Titles = Titles.substring(0, Titles.indexOf(":"));
		Title.setText(Titles);

		TextView Campus = (TextView) convertView
				.findViewById(R.id.timetable_customlist_campus);
		Campus.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS]);

		TextView Cate = (TextView) convertView
				.findViewById(R.id.timetable_customlist_cate);
		Cate.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_03_CATE3]);

		TextView credit = (TextView) convertView
				.findViewById(R.id.timetable_customlist_credit);
		credit.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_09_CREDIT]);

		TextView group = (TextView) convertView
				.findViewById(R.id.timetable_customlist_group);
		group.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_06_GROUP]);

		TextView lecid = (TextView) convertView
				.findViewById(R.id.timetable_customlist_lecid);
		lecid.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID]);

		TextView professor = (TextView) convertView
				.findViewById(R.id.timetable_customlist_professor);
		professor
				.setText(arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_08_PROFESSOR]);

		TextView schedule = (TextView) convertView
				.findViewById(R.id.timetable_customlist_schedule);
		String secheduleStr = arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE];
		secheduleStr = secheduleStr.replaceAll(":", " / ");
		secheduleStr = secheduleStr.replace(")", ")  ");

		String tmpstr = arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME];
		if (tmpstr.indexOf(":") != -1) {
			tmpstr = tmpstr.substring(tmpstr.indexOf(":") + 1);
			secheduleStr = secheduleStr + "\n" + tmpstr;
		}

		tmpstr = "\n상대평가 : "
				+ arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_13_REMARKS1]
				+ " / 인원제한 : "
				+ arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_14_REMARKS2]
				+ " / 대기 : "
				+ arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_15_REMARKS3]
				+ " / 교환학생 : "
				+ arSrc.get(position)[DataBaseHelper.DB_INDEX_3ROW_16_REMARKS4];
		secheduleStr = secheduleStr + tmpstr;

		schedule.setText(secheduleStr);

		convertView.setTag(position);
		return convertView;
	}

}