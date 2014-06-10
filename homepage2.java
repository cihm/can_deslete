package com.fengjian.test;

import imageProcess.ImageProcess;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.fengjian.test.homepage_TabPjtIng.ProjectInformationCheck;
import com.fengjian.test.homepage_TabPjtIng.SpinnerSelectedListener;

import json.Data_info_gson_P_itemlist;
import json.Data_info_gson_P_teams;
import json.Data_info_gson_Plist;
import json.Data_info_gson_ProjectData;
import junit.framework.Test;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;


public class homepage2 extends Activity {
	private EditText user,password;
	private CheckBox cover,non_cover;
    private Button login ;
    private LinearLayout layout_login,layout_after_login ;
    private Spinner upload_spin, download_spin;
    private ArrayAdapter upload_spin_adapter , download_spin_adapter;
    private ArrayList<String> upload_spinnerP = new ArrayList<String>();
    private ArrayList<String> download_spinnerP = new ArrayList<String>();
    private ArrayList<String> download_spinnerP_id = new ArrayList<String>();
    private ArrayList<String> team_dbname = new ArrayList<String>();
    private String tableName = "homepage";
    private String tablename ="tCustomers";
    public String dbname,db_name;
    private Context context = null;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0; 
    public static final int DIALOG_UPLOAD_PROGRESS  = 1;
    public static final int DIALOG_LOGIN_PROGRESS  = 2;
    private ProgressDialog mpDialog = null ;
    private InputMethodManager imm;
    private ImageProcess imageProcess ;
    private String p_name;
    private ArrayList<String> list = new ArrayList<String>();
    private HashMap<String, String> project_data = new HashMap<String, String>();
    private ArrayList<String> item = new ArrayList<String>();
    private ArrayList<String> imageSaveOrNot = new ArrayList<String>(); //imane name;
	private ArrayList<Integer> color_src = new ArrayList<Integer>();
	private ArrayList<HashMap<String,String>> teamInfo = new ArrayList<HashMap<String,String>>();
	private ArrayList<ArrayList<HashMap<String,String>>> team_work_List = new ArrayList<ArrayList<HashMap<String,String>>>();
   
	private TextView  needToDeletePjTextView ;
	private   Animation animFadein;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        
        DefaultExceptionHandler crashHandler = DefaultExceptionHandler.getInstance();  
        crashHandler.init(this);  //need  pass Activity， or AlertDialog  won't show
        
        
        context = this;
        SharedPreferences settings2 = getSharedPreferences ("know_leave", 0);
		SharedPreferences.Editor PE2 = settings2.edit();
		PE2.putString("know_leave_tag", "2");
		PE2.commit();
		
        String strVer= android.os.Build.VERSION.RELEASE;
	     strVer=strVer.substring(0,3).trim();
	     Log.e("homepage2", strVer);
	     float fv=Float.valueOf(strVer);
	    
	     if(fv<4.0){
		     StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		     	.detectDiskReads()     
		        .detectDiskWrites()     
		        .detectNetwork()   // or .detectAll() for all detectable problems     
		        .penaltyLog()     
		        .build());     
		     StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()     
		        .detectLeakedSqlLiteObjects()     
		        .detectLeakedClosableObjects()     
		        .penaltyLog()     
		        .penaltyDeath()     
		        .build());
	     }
        
        cover = (CheckBox) findViewById( R.id.checkBox2 );
        non_cover = (CheckBox) findViewById( R.id.checkBox1 );
        user=(EditText)findViewById(R.id.editText1);
        password=(EditText)findViewById(R.id.editText2);
        needToDeletePjTextView = (TextView)findViewById(R.id.neeToDeletePj);
        layout_login = (LinearLayout)findViewById(R.id.loglayout);
        layout_after_login = (LinearLayout)findViewById(R.id.afterlogin);  
        layout_after_login.setVisibility(View.GONE);
        login = (Button)findViewById(R.id.login);
        Set_Upload_SpinnerItem();
        login.setOnClickListener(loginoOnClickListener);
        
      
        
        //download will put in after connect to server
        upload_spin = (Spinner) findViewById(R.id.spinner1); 
        upload_spin_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,upload_spinnerP);  
        upload_spin_adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);  //spinner style set
        upload_spin.setAdapter(upload_spin_adapter); 
	    upload_spin.setSelection( upload_spinnerP.size()-1);
	     
	    upload_spin.setOnItemSelectedListener(new Upload_SpinnerSelectedListener());  
	    upload_spin.setVisibility(View.VISIBLE);   //set deauflt
	     
	    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	     
	    imageProcess = new ImageProcess(this);
        
	    SharedPreferences settings = getSharedPreferences("data", 0);
	    user.setText(settings.getString("user", ""));
	    password.setText(settings.getString("password", ""));
	   
     }
     
   
	



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
			   
		 super.onSaveInstanceState(outState);
	}

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
     
      String StrTest = savedInstanceState.getString("user");
      Log.e("homepage2", StrTest);
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mpDialog = new ProgressDialog(homepage2.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��Ϊ�������
			mpDialog.setTitle("下載ʾ");// ���ñ��� 29
			// mpDialog.setIcon(R.drawable.icon);//����ͼ��
			mpDialog.setMessage("下載專題中");
			mpDialog.setIndeterminate(false);// ���ý�����Ƿ�Ϊ����ȷ false
												// ���ǲ�����Ϊ����ȷ
			mpDialog.setCancelable(false);// ���ý�����Ƿ���԰��˻ؼ�ȡ��
			mpDialog.setProgress(0);
			mpDialog.incrementProgressBy(1); // ���Ӻͼ��ٽ�ȣ�������Ա����

			/*
			 * mpDialog.setButton("ȷ��", new DialogInterface.OnClickListener(){
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * {
			 * 
			 * mpDialog.dismiss(); } });
			 */
			mpDialog.show();
			return mpDialog;

		case DIALOG_UPLOAD_PROGRESS:
			mpDialog = new ProgressDialog(homepage2.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��Ϊ�������
			mpDialog.setTitle("上傳");// ���ñ��� 29
			// mpDialog.setIcon(R.drawable.icon);//����ͼ��
			mpDialog.setMessage("上傳專題中");
			mpDialog.setIndeterminate(false);// ���ý�����Ƿ�Ϊ����ȷ false
												// ���ǲ�����Ϊ����ȷ
			mpDialog.setCancelable(false);// ���ý�����Ƿ���԰��˻ؼ�ȡ��
			mpDialog.setProgress(0);
			mpDialog.incrementProgressBy(1); // ���Ӻͼ��ٽ�ȣ�������Ա����

			mpDialog.show();

			return mpDialog;

		case DIALOG_LOGIN_PROGRESS:
			mpDialog = new ProgressDialog(homepage2.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��Ϊ�������
			mpDialog.setTitle("登入中");// ���ñ��� 29
			// mpDialog.setIcon(R.drawable.icon);//����ͼ��
			mpDialog.setMessage("登入中");
			mpDialog.setIndeterminate(false);// ���ý�����Ƿ�Ϊ����ȷ false
												// ���ǲ�����Ϊ����ȷ
			mpDialog.setCancelable(false);// ���ý�����Ƿ���԰��˻ؼ�ȡ��
			mpDialog.setProgress(0);
			mpDialog.incrementProgressBy(1); // ���Ӻͼ��ٽ�ȣ�������Ա����

			/*
			 * mpDialog.setButton("ȷ��", new DialogInterface.OnClickListener(){
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * {
			 * 
			 * mpDialog.dismiss(); } });
			 */
			mpDialog.show();
			return mpDialog;

		default: {
			return null;
		}

		}
	}
	  
	 	private void Set_Upload_SpinnerItem() {
	 		upload_spinnerP.clear();
	 		team_dbname.clear();
			SQLitehomedata 	dbHelper = new SQLitehomedata(this, tableName, tableName);
			Cursor cursor =dbHelper.getAllContacts(tableName);
		   
		
			if(cursor.moveToFirst()){
				do{
					
					upload_spinnerP.add(cursor.getString(1));
					team_dbname.add(cursor.getString(3));
				}while(cursor.moveToNext());
			} upload_spinnerP.add("尚未選擇專題");
			  team_dbname.add("尚未選擇專題");
		    
	
			 dbHelper.close();
	         cursor.close();
	         
	         //Log.e("size","size"+upload_spinnerP.size());
	         
		}
	 
	
	 	
	
	 
     
		  private Button.OnClickListener loginoOnClickListener
		     = new Button.OnClickListener(){
		
		   public void onClick(View arg0) {
			   
			   if(haveInternet()!=true)
			   {
				   AlertDialog.Builder builder = new AlertDialog.Builder(homepage2.this);
					  builder.setMessage("目前沒有網路")
							.setCancelable(false)
							.setPositiveButton("確定",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
									
											//upload
											
											     
										}
									});
						
					  Window window =  builder.show().getWindow();
					   
		          	  WindowManager.LayoutParams lp = window.getAttributes();  
		        	  lp.alpha = 0.6f;  
	                  window.setAttributes(lp);

			   }
			   else
			   {   
				   if(!(user.getText().toString().trim().length()<1) || !(password.getText().toString().trim().length()<1))
				   //if(!user.getText().toString().equals("") || !password.getText().toString().equals(""))
				   {
					   
					showDialog(DIALOG_LOGIN_PROGRESS); 
					
				    UserLoginTask mAuthTask = new UserLoginTask();
				   
				    mAuthTask.execute(user.getText().toString().trim(), password.getText().toString().trim());
				    
				    imm.hideSoftInputFromWindow(user.getWindowToken(), 0);
				   }
				   else
				   {      AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
		        	      delAlertDialog.setTitle("請輸入帳號密碼");
			              Window window =  delAlertDialog.show().getWindow();
			          	  WindowManager.LayoutParams lp = window.getAttributes();  
			        	  lp.alpha = 0.6f;  
			       	      lp.width=300;
			        	  window.setGravity(Gravity.CENTER);
			        	  window.setAttributes(lp);
			        	  
				   }  
				   
			   }
			   
			   
		   }
		   
		  };    
     
     
		  public class UserLoginTask extends AsyncTask<String, Void, ArrayList<String> > {
			  
			    @Override
			    protected void onPostExecute(final ArrayList<String> status_result) {
			        
			    	mpDialog.cancel();
			    	
			    	download_spinnerP.clear();
			    	download_spinnerP_id.clear();
			    	if (status_result.get(0).equals("yes")) {
			        	
			    		
			    		//Log.e("mds[0]  mds[1]",status_result.get(1));
			    		//
			    		
			    		Gson gson = new Gson();
						
						Type type = new TypeToken<List<Data_info_gson_Plist>>(){}.getType(); 
						
						 try
						 {
							 List<Data_info_gson_Plist> testBeanListFromJson = gson.fromJson(status_result.get(1), type); 
							   
							    SharedPreferences settings = getSharedPreferences ("data", 0);
					    		SharedPreferences.Editor PE = settings.edit();
					    		PE.putString("user", user.getText().toString());
					    		PE.putString("password",password.getText().toString());
					    		PE.commit();
					    		animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up); 
					    		
					    		layout_login.setAnimation(animFadein);
					    		layout_login.setVisibility(View.GONE); 
					    		
					    		animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down); 
					    		layout_after_login.setAnimation(animFadein);
					    		layout_after_login.setVisibility(View.VISIBLE);
								
							    // 1 is projectname  2 is ...
								for(int i = 0 ; i < testBeanListFromJson.size() ; i ++)
									{
									 download_spinnerP.add(testBeanListFromJson.get(i).get_project_subject());
								     download_spinnerP_id.add(testBeanListFromJson.get(i).get_project_id());
									}
								
								 download_spinnerP_id.add("尚未選擇專題");
								 download_spinnerP.add("尚未選擇專題");
					    		 download_spin = (Spinner) findViewById(R.id.spinner2); 
					    		 download_spin_adapter = new ArrayAdapter<String>(homepage2.this,android.R.layout.simple_spinner_item,download_spinnerP);  
					    		 download_spin_adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);  //spinner style set
					    		 download_spin.setAdapter(download_spin_adapter); 
					    		 download_spin.setSelection( download_spinnerP.size()-1);
					    		 download_spin.setOnItemSelectedListener(new Download_SpinnerSelectedListener());  
						
						 
						 }
					     catch(Exception e)
					     {
					    	 AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
				        	  
				        	  delAlertDialog.setTitle("請使用正確的無線網路");
				              //delAlertDialog.setMessage(" : "+idea[position]);
				        	  Window window =  delAlertDialog.show().getWindow();
				          	  WindowManager.LayoutParams lp = window.getAttributes();  
				        	  lp.alpha = 0.6f;  
				       	      lp.width=300;
				        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
				        	  window.setAttributes(lp);
				        	
					     }
							   
					
			    		
			    		
			        } else {
			        	 AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
			        	  
			        	  delAlertDialog.setTitle("帳號密碼錯誤");
			              //delAlertDialog.setMessage(" : "+idea[position]);
			        	  Window window =  delAlertDialog.show().getWindow();
			          	  WindowManager.LayoutParams lp = window.getAttributes();  
			        	  lp.alpha = 0.6f;  
			       	      lp.width=300;
			        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
			        	  window.setAttributes(lp);
			        	  
			        	  user.setText("");
			        	  password.setText("");
			        }
			    	
			    	if(upload_spinnerP.size() == 8+1 )
			         {
			        	 needToDeletePjTextView.setVisibility(View.VISIBLE);
			        	 needToDeletePjTextView.setTextColor(Color.RED);
			        	 download_spin.setClickable(false);
			        	 download_spin.setEnabled(false);
			         }
			    }

			    @Override
			    protected ArrayList<String> doInBackground(String... login)
			     {
			    	HttpClient client = new DefaultHttpClient();
					String url = "http://cetools.csie.ntnu.edu.tw/intel/app/get_project_list.php";
					HttpGet get = new HttpGet(url+"?"+"username="+login[0]+"&"+"password="+login[1]+"&"+"project_type=VR");
					String result =null;
					ArrayList<String> statusResult = new ArrayList<String>();
					statusResult.add("no");
					Log.e("login[0]", login[0]+"---"+login[1]);
			        try {  
					    HttpResponse response = client.execute(get);
					    
					   // Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode()); //?ȡ???  
					   // Log.i("result", "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//?ȡ��?��???��  
					 
					    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					    {   
					    	HttpEntity resEntity = response.getEntity();
					    	
					   	
					    	if(resEntity != null)
					    	{   
					    		result = EntityUtils.toString(resEntity , HTTP.UTF_8);
					    		statusResult.set(0,"yes");
					    		
					    	}
					    	
					    	
					    	
					    }
					    else
					    {
					      HttpEntity resEntity = response.getEntity();
					      if(resEntity != null)
				    	   {   
				    		  result = EntityUtils.toString(resEntity , HTTP.UTF_8);
				    		  statusResult.set(0,"no");
				    	   }
					    }
				   
				   
				    } catch (ClientProtocolException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					} catch (IOException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					}
			        
			        
			        statusResult.add(result);
			        
			        if( statusResult.get(0).equals("yes"))
			        {
			        	return  statusResult;
			        }
			        else
			        {
			        	return  statusResult;
			        }
			        
			     
			    }
			    
		  }    
			    
		  
		  public class GetProjectData_Task extends AsyncTask<String, Void, ArrayList<String> > {
			  
			    @Override
			    protected void onPostExecute(final ArrayList<String> status_result) {
			        
			    	list.clear();
			    	if (status_result.get(0).equals("yes")) {
			        	
			    		/*gson nest class not work , search if have time
			    		Gson gson = new Gson();
			    		Type type = new TypeToken<Data_info_gson_ProjectData>(){}.getType(); 
						Data_info_gson_ProjectData testBeanListFromJson = gson.fromJson(status_result.get(1), type); 
						
						
							Log.e("get_project_id", testBeanListFromJson.get_project_id());
							Log.e("get_project_subject", testBeanListFromJson.get_project_subject());
							Log.e("get_project_description", testBeanListFromJson.get_project_description());
							Log.e("get_project_question", testBeanListFromJson.get_project_question());
							Log.e("get_teacher_id", testBeanListFromJson.get_teacher_id());
							Log.e("get_project_type", testBeanListFromJson.get_project_type());
							Log.e("get_project_last_edit", testBeanListFromJson.get_project_last_edit());
							Log.e("get_project_last_sync", testBeanListFromJson.get_project_last_sync());
							Log.e("get_project_upload_times", testBeanListFromJson.get_project_upload_times());
							Log.e("get_project_download_times", testBeanListFromJson.get_project_download_times());
							Log.e("get_project_activatedTimeMillis", testBeanListFromJson.get_project_activatedTimeMillis());
						
							
							Log.e("get_item_list", testBeanListFromJson.get_item_list().toString());
							//Log.e("item_list().get(0)", ((Data_info_gson_P_itemlist) testBeanListFromJson.get_item_list().get(0)).get_title());
							Log.e("item_list().get(0)", testBeanListFromJson.get_item_list().get(0).toString());
							Log.e("get_teams", testBeanListFromJson.get_teams().toString());
							Log.e("teams().get(0)", testBeanListFromJson.get_teams().get(0).toString());
							*/
							//======================================================================================
			    	
							try {
							     project_data.clear();
				    		     item.clear();
								 color_src.clear();
				    		     
				    		     JsonParser parser = new JsonParser();
							     JsonObject obj = (JsonObject)parser.parse(status_result.get(1));
							     project_data.put("project_id", obj.get("project_id").getAsString());
							     project_data.put("project_subject", obj.get("project_subject").getAsString());
							     project_data.put("project_description", obj.get("project_description").getAsString());
							     project_data.put("teacher_id", obj.get("teacher_id").getAsString());
							     project_data.put("project_question", obj.get("project_question").getAsString());
							     project_data.put("project_type", obj.get("project_type").getAsString());
							     project_data.put("project_last_edit", obj.get("project_last_edit").getAsString());
							     project_data.put("project_last_sync", obj.get("project_last_sync").getAsString());
							     project_data.put("project_upload_times", obj.get("project_upload_times").getAsString());
							     project_data.put("project_download_times", obj.get("project_download_times").getAsString());
							     project_data.put("project_activatedTimeMillis", obj.get("project_activatedTimeMillis").getAsString());
				    		     
				    		   
								 
								 
								 parser = new JsonParser();
							     obj = (JsonObject)parser.parse(status_result.get(1));
							        //JsonElement id = obj.get("id");
							        // System.out.println(id); // Prints "10001"				 
							     JsonArray arr = obj.get("item_list").getAsJsonArray();
							     
							     for(int i = 0 ; i <  arr.size() ; i++)
							     {   
							    	  JsonElement p = arr.get(i);
								      Log.e("item_list.get(0)", p.toString()); 
								    
								      parser = new JsonParser();
								      obj = (JsonObject)parser.parse(p.toString());
								      JsonElement title = obj.get("title");
								      JsonElement color = obj.get("color");
								     // p = arr.get(0);
								      Log.e("title", title.toString()+"==="+color.toString()); 
								      
								      item.add(title.getAsString());//item list 
								      list.add(title.getAsString());
								      
								      parser = new JsonParser();
								      obj = (JsonObject)parser.parse(color.toString());
								      JsonElement r = obj.get("r");
								      JsonElement g = obj.get("g");
								      JsonElement b = obj.get("b");
								      Log.e("rgb", r.getAsInt()+"==="+g.getAsInt()+"==="+b.getAsInt());
								      
								      color_src.add(Color.rgb( r.getAsInt(),  g.getAsInt(),  b.getAsInt()));
								    
								    
							     }
							     
								
							     teamInfo.clear();
							     team_work_List.clear(); 
							     
							     obj = (JsonObject)parser.parse(status_result.get(1));
							     JsonArray teams_arr = obj.get("teams").getAsJsonArray();
							     
							     for(int i = 0 ; i <  teams_arr.size() ; i++)
							     {   
							    	  JsonElement p = teams_arr.get(i);
								      //Log.e("item_list.get(0)", p.toString()); 
								    
								      parser = new JsonParser();
								      obj = (JsonObject)parser.parse(p.toString());
								      JsonElement team_info = obj.get("team_info");
								      
								      parser = new JsonParser();
								      obj = (JsonObject)parser.parse(team_info.toString());
								      JsonElement name = obj.get("name");
								      JsonElement password = obj.get("password");
								      JsonElement member = obj.get("member");
								      JsonElement work_editing_timestamp = obj.get("work_editing_timestamp");
								      
								      Log.e("member", name.getAsString()+"=="+password.getAsString());
								      HashMap<String, String> cMap = new HashMap<String, String>();
								      cMap.put("name",     name.getAsString());
							          cMap.put("password", password.getAsString());
							          cMap.put("member",   member.getAsString());
							          cMap.put("work_editing_timestamp", work_editing_timestamp.getAsString());
							          teamInfo.add(cMap);
								       
							          obj = (JsonObject)parser.parse(p.toString());
							       
							          JsonArray team_work_arr = obj.get("team_work").getAsJsonArray();
							          
							          ArrayList<HashMap<String,String>> team_work_Listt = new ArrayList<HashMap<String,String>>();
							          
							          for(int j = 0 ; j<  team_work_arr.size() ; j++)
							          {
							        	  p = team_work_arr.get(j);
									      Log.e("team_work_arr", p.toString());
									    
									       parser = new JsonParser();
									       obj = (JsonObject)parser.parse(p.toString());
									       JsonElement title = obj.get("title");
									       JsonElement reason = obj.get("reason");
									       //Log.e("reason", title.getAsString()+"=="+reason.getAsString());
									       HashMap<String, String> iMap = new HashMap<String, String>();
									       iMap.put("title",     title.getAsString());
									       iMap.put("reason",     reason.getAsString());
									       team_work_Listt.add(iMap);
									       
							          }
							          
								     
							          team_work_List.add(team_work_Listt);
							         
								    
								    
							     }
							} catch (Exception e) {
								 AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
					        	  
					        	  delAlertDialog.setTitle("下載錯誤");
					              delAlertDialog.setMessage("請確定伺服器端是否為完整資料");
					        	  Window window =  delAlertDialog.show().getWindow();
					          	  WindowManager.LayoutParams lp = window.getAttributes();  
					        	  lp.alpha = 0.6f;  
					       	      lp.width=300;
					        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
					        	  window.setAttributes(lp);
							}
						   
						     
						 	 GetProjectImage_Task mAuthTaskImage = new GetProjectImage_Task();
							   
							 mAuthTaskImage.execute(user.getText().toString(), password.getText().toString(),p_name);//project id
							
							
			    		
			        } else {
			        	 AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
			        	  
			        	  delAlertDialog.setTitle("下載失敗");
			              //delAlertDialog.setMessage(" : "+idea[position]);
			        	  Window window =  delAlertDialog.show().getWindow();
			          	  WindowManager.LayoutParams lp = window.getAttributes();  
			        	  lp.alpha = 0.6f;  
			       	      lp.width=300;
			        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
			        	  window.setAttributes(lp);
			        	  
			        	  //user.setText("");
			        	 // password.setText("");
			        }
			    }

			    @Override
			    protected ArrayList<String> doInBackground(String... login)
			     {
			    	HttpClient client = new DefaultHttpClient();
					String url = "http://cetools.csie.ntnu.edu.tw/intel/app/get_project.php";
					HttpGet get = new HttpGet(url+"?"+"username="+login[0]+"&"+"password="+login[1]+"&"+"project_id="+login[2]);
					String result =null;
					ArrayList<String> statusResult = new ArrayList<String>();
					statusResult.add("no");
					Log.e("doInBackground", url+"?"+"username="+login[0]+"&"+"password="+login[1]+"&"+"project_id="+login[2]);
					Log.e("doInBackground", login[2]);
					
			        try {  
					    HttpResponse response = client.execute(get);
					    
					   // Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode()); //?ȡ???  
					   // Log.i("result", "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//?ȡ��?��???��  
					    
					    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					    {   
					    	HttpEntity resEntity = response.getEntity();
					    	
					   	
					    	if(resEntity != null)
					    	{   
					    		result = EntityUtils.toString(resEntity , HTTP.UTF_8);
					    		statusResult.set(0,"yes");
					    		// Log.e(".....yes...", result);
					    	}
					    	
					    	
					    	
					    }
					    else
					    {
					      HttpEntity resEntity = response.getEntity();
					      if(resEntity != null)
				    	   {   
				    		  result = EntityUtils.toString(resEntity , HTTP.UTF_8);
				    		  statusResult.set(0,"no");
				    		  //Log.e("..no......", result);
				    	   }
					    }
				   
				   
				    } catch (ClientProtocolException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					} catch (IOException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					}
			        
			        
			        statusResult.add(result);
			       // Log.e(".........", result);
			        if( statusResult.get(0).equals("yes"))
			        {
			        	return  statusResult;
			        }
			        else
			        {
			        	return  statusResult;
			        }
			        
			     
			    }
			    
		  }    
		  
		  
		public Handler handl_more_pj = new Handler() {
				@Override
				public void handleMessage(Message msg) {
				
					
					
				
					
			if (msg.getData().getInt("count") == 8) {

				Set_Upload_SpinnerItem();
				upload_spin_adapter.notifyDataSetChanged();
				upload_spin.setAdapter(upload_spin_adapter);

				upload_spin.setSelection(upload_spinnerP.size() - 1);

				needToDeletePjTextView.setVisibility(View.VISIBLE);
				needToDeletePjTextView.setTextColor(Color.RED);
				download_spin.setClickable(false);
				download_spin.setEnabled(false);
			} else {
				Set_Upload_SpinnerItem();
				upload_spin_adapter.notifyDataSetChanged();
				upload_spin.setAdapter(upload_spin_adapter);

				upload_spin.setSelection(upload_spinnerP.size() - 1);
			}
			         
				}
			};

		  
		  public class GetProjectImage_Task extends AsyncTask<String, Void, ArrayList<String> > {
			  
			    @Override
			    protected void onPostExecute(final ArrayList<String> status_result) {
			    	
			    	
			    	
			    	imageSaveOrNot.clear();
			    	
			    	for(int i = 0 ; i< list.size() ; i++)    
			    	{
			    		if (!status_result.get(i).equals("no")) {
				        	
					    	
							//======================================================================================
			    		     HashMap<String, String> projectImage = new HashMap<String, String>();
			    		     
			    		     JsonParser parser = new JsonParser();
						     JsonObject obj = (JsonObject)parser.parse(status_result.get(i));
					
						    
						     
							if(obj.get("image_string").getAsString()!=null && !obj.get("image_string").getAsString().equals(""))
						    {
								
								ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
			        	    	File directory = cw.getDir(obj.get("image_name").getAsString(), Context.MODE_PRIVATE);
			        	    	File file = new File(directory,obj.get("image_name").getAsString()+".png");
			        	    	boolean deleted = file.delete();
			        		    if(deleted)
			        		        Log.e("del",obj.get("image_name").getAsString());
			        		    else
			        		    	Log.e("not del",obj.get("image_name").getAsString());
			        		    
			        		    
								
								
								//Log.e("image name", obj.get("image_string").getAsString()+"=="+obj.get("image_name").getAsString());
								imageProcess.saveImageToInternalStorage( imageProcess.decodeBase64(obj.get("image_string").getAsString()), obj.get("image_name").getAsString());
								imageSaveOrNot.add( obj.get("image_name").getAsString());
						    }
							else
							{
								imageSaveOrNot.add("0");
							}
		                     
							
	  
							
							
						    // Save_DownloadData save_downloaddata = new Save_DownloadData(context, );
						    // save_downloaddata.Save(mpDialog);
						     
						   
						   
			    		
			        } else {
			        	/* AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
			        	  
			        	  delAlertDialog.setTitle("�e�`");
			              //delAlertDialog.setMessage(" : "+idea[position]);
			        	  Window window =  delAlertDialog.show().getWindow();
			          	  WindowManager.LayoutParams lp = window.getAttributes();  
			        	  lp.alpha = 0.6f;  
			       	      lp.width=300;
			        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
			        	  window.setAttributes(lp);
			        	  */
			        	  //user.setText("");
			        	 // password.setText("");
			        }
			    	}
			    	
			    	
			    	  Save_DownloadData save_downloaddata = new Save_DownloadData(context,project_data,item,color_src,teamInfo,team_work_List,imageSaveOrNot );
					  save_downloaddata.Save(mpDialog ,handl_more_pj);
					  
			    }

			    @Override
			    protected ArrayList<String> doInBackground(String... login)
			     {
			    	  ArrayList<String> statusResult = new ArrayList<String>();
			    	  
			    	
				        
				    for(int i = 0 ; i< list.size() ; i++)    
				    {
				    	HttpClient client = new DefaultHttpClient();
						String url = "http://cetools.csie.ntnu.edu.tw/intel/app/get_vr_image.php";
						HttpGet get = new HttpGet(url+"?"+"username="+login[0]+"&"+"project_id="+login[2]+"&"+"title="+URLEncoder.encode(list.get(i)));
						String result =null;
						
						Log.e("get",get.getURI().toString());
					
				        try {  
						    HttpResponse response = client.execute(get);
						    
						   // Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode()); //?ȡ???  
						   // Log.i("result", "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//?ȡ��?��???��  
						    
						    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
						    {   
						    	HttpEntity resEntity = response.getEntity();
						    	
						   	
						    	if(resEntity != null)
						    	{   
						    		result = EntityUtils.toString(resEntity , HTTP.UTF_8);
						    		statusResult.add(result);
						            Log.e(".....yes...", result);
						    	}
						    	
						    	
						    	
						    }
						    else
						    {
						      HttpEntity resEntity = response.getEntity();
						      if(resEntity != null)
					    	   {   
					    		  result = EntityUtils.toString(resEntity , HTTP.UTF_8);
					    		  statusResult.add("no");
					    		  Log.e("..no......", result);
					    	   }
						    }
					   
					   
					    } catch (ClientProtocolException e) {  
						    // TODO Auto-generated catch block  
						    e.printStackTrace();  
						} catch (IOException e) {  
						    // TODO Auto-generated catch block  
						    e.printStackTrace();  
						}
				        
				        
				      
				       // Log.e(".........", result);
				   
				        
				        	
				       }
				    
				    return  statusResult;
				    
				    }
			        
			     
			    }
			    
		      
		  
		   
		  
		/*  public class UploadProjectData_Task extends AsyncTask<String, Void, ArrayList<String> > {
			  
			    @Override
			    protected void onPostExecute(final ArrayList<String> status_result) {
			        
			    	
			    	if (status_result.get(0).equals("yes")) {
			        	
			    	
						   
			    		
			        } else {
			        	 AlertDialog.Builder delAlertDialog = new AlertDialog.Builder(homepage2.this);
			        	  
			        	  delAlertDialog.setTitle("�e�`");
			              //delAlertDialog.setMessage(" : "+idea[position]);
			        	  Window window =  delAlertDialog.show().getWindow();
			          	  WindowManager.LayoutParams lp = window.getAttributes();  
			        	  lp.alpha = 0.6f;  
			       	      lp.width=300;
			        	  window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM); 
			        	  window.setAttributes(lp);
			        	  
			        	  //user.setText("");
			        	 // password.setText("");
			        }
			    }

			    @Override
			    protected ArrayList<String> doInBackground(String... login)
			     {
			    	HttpClient client = new DefaultHttpClient();
					String url = "http://cetools.csie.ntnu.edu.tw/intel/app/get_project.php";
					HttpGet get = new HttpGet(url+"?"+"username="+login[0]+"&"+"password="+login[1]+"&"+"project_id="+login[2]);
					String result =null;
					ArrayList<String> statusResult = new ArrayList<String>();
					statusResult.add("no");
					Log.e("doInBackground", url+"?"+"username="+login[0]+"&"+"password="+login[1]+"&"+"project_id="+login[2]);
					Log.e("doInBackground", login[2]);
					
			        try {  
					    HttpResponse response = client.execute(get);
					    
					   // Log.i("resCode", "resCode = " + response.getStatusLine().getStatusCode()); //?ȡ???  
					   // Log.i("result", "result = " + EntityUtils.toString(response.getEntity(), "utf-8"));//?ȡ��?��???��  
					    
					    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					    {   
					    	HttpEntity resEntity = response.getEntity();
					    	
					   	
					    	if(resEntity != null)
					    	{   
					    		result = EntityUtils.toString(resEntity , HTTP.UTF_8);
					    		statusResult.set(0,"yes");
					    		// Log.e(".....yes...", result);
					    	}
					    	
					    	
					    	
					    }
					    else
					    {
					      HttpEntity resEntity = response.getEntity();
					      if(resEntity != null)
				    	   {   
				    		  result = EntityUtils.toString(resEntity , HTTP.UTF_8);
				    		  statusResult.set(0,"no");
				    		  //Log.e("..no......", result);
				    	   }
					    }
				   
				   
				    } catch (ClientProtocolException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					} catch (IOException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					}
			        
			        
			        statusResult.add(result);
			       // Log.e(".........", result);
			        if( statusResult.get(0).equals("yes"))
			        {
			        	return  statusResult;
			        }
			        else
			        {
			        	return  statusResult;
			        }
			        
			     
			    }
			    
		  }    
		  */
		  
		  class Upload_SpinnerSelectedListener implements OnItemSelectedListener
		   {   
		     	  
		         public void onItemSelected(AdapterView<?> adapter, View arg1, final int arg2, long arg3) {  
		          
		          if(upload_spinnerP.get(arg2).equals("尚未選擇專題"))
		          {}
		          else
		          {
		        	 AlertDialog.Builder builder = new AlertDialog.Builder(homepage2.this);
					  builder.setMessage("確定要上傳此專題")
							.setCancelable(false)
							.setTitle("您所選擇的專題為: "+upload_spinnerP.get(arg2))
							.setPositiveButton("確定",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
									
											dbname=team_dbname.get(arg2)+team_dbname.get(arg2);
											db_name =team_dbname.get(arg2);
											String p_name = upload_spinnerP.get(arg2);
											//upload
											//UploadProjectData_Task mAuthTask11 = new UploadProjectData_Task();
											   
											//mAuthTask11.execute(user.getText().toString(), password.getText().toString(),download_spinnerP_id.get(arg2));//project id
											showDialog(DIALOG_UPLOAD_PROGRESS); 
											Upload_tabletData uploaddata = new Upload_tabletData(context,db_name,p_name,user.getText().toString(),password.getText().toString(),imageProcess);
											
											uploaddata.Upload(mpDialog);
											/*
											//----get teacher own data------------------------------------
									        SQLitelistdata l_manager = new SQLitelistdata(homepage2.this,tablename,db_name);
									        Cursor cursor =l_manager.getAllContacts(tablename);
									       
									        int key_inni=0;
									     
									        int cont_list = 0;
									        ArrayList<String> list = new ArrayList<String>();
									        ArrayList<Integer> color_src = new ArrayList<Integer>();
											ArrayList<String> idea = new ArrayList<String>();
								            String intro, hint;
								           
									        if(cursor.moveToFirst()){

									        		
									             do{
									            	if( cursor.getString(1).equals( p_name))
									            	{//   cursor.getString(0)+" "+         _id
									              
									            	 cont_list=Integer.parseInt(cursor.getString(2));          //cont
									            	 list.add(cursor.getString(3));                            //title
									                 color_src.add(Integer.parseInt(cursor.getString(4)));//color
									          	     idea.add(cursor.getString(5));                       //idea    
									          	     intro=cursor.getString(6);
									          	     hint=cursor.getString(7);
									          	    
									          	     key_inni++;
									            	} 
									             }while(cursor.moveToNext());
									        }       
									       
									        l_manager.close();
									        cursor.close();
									       
									        ArrayList<HashMap<String,Integer>> color = new ArrayList<HashMap<String,Integer>>();
									        
									       
									        for(int i = 0 ; i< cont_list ; i++)
									        { 
									        	
									          HashMap<String, Integer> cMap = new HashMap<String, Integer>();
									          cMap.put("red",Color.red(color_src.get(i)));
									          cMap.put("green",Color.green(color_src.get(i)));
									          cMap.put("blue",Color.blue(color_src.get(i)));
									          cMap.put("alpha",Color.alpha(color_src.get(i)));
									          color.add(cMap);
									          
									          //this is back to int 
									          //Color.argb(Color.alpha(color_src[i]), Color.red(color_src[i]), Color.green(color_src[i]), Color.blue(color_src[i])));
									        }
									        
											//-----------------------------------------------------------------
											//get team data --------------------------------------------------------------------------------
											
											 SQLiteTeamdata l_manager_team = new SQLiteTeamdata(homepage2.this,tablename,dbname);
									         Cursor cursor2_team =l_manager_team.getAllContacts(tablename);
									       
									         ArrayList<String> team_name = new ArrayList<String>();
									         ArrayList<String> account  = new ArrayList<String>();
									         ArrayList<String> password = new ArrayList<String>();
									         ArrayList<String> team_member = new ArrayList<String>();
									         int number_of_team = 0;
									         
									         if(cursor2_team.moveToFirst()){
									       	 
									              do{
									             
									            	   number_of_team=Integer.parseInt(cursor2_team.getString(2));          //cont
									           	       team_name.add(cursor2_team.getString(3));
									           	       account.add(cursor2_team.getString(4)); 
									           	       password.add(cursor2_team.getString(5)); 
									                   team_member.add(cursor2_team.getString(6));
									           	
									              }while(cursor2_team.moveToNext());
									         }       
									        
									         l_manager_team.close();
									         cursor2_team.close();
										   //---------------------------------------------------
									         
									       //-------get each student data  
									         for(int i=0 ;i<number_of_team;i++)
										     { 
										    	 
										    	      SQLite_student_list_data  l_manager2 = new SQLite_student_list_data(homepage2.this,tablename,db_name+team_name.get(i));
												      l_manager2.getReadableDatabase();
												      Cursor cursor2 =l_manager2.getAllContacts(tablename);
												       key_inni=0;
												        if(cursor2.moveToFirst()){
												        	//  list3.clear();
												    		
												              do{
												        	     if( cursor2.getString(1).equals(  db_name+team_name.get(i)))
												        	     {//   cursor.getString(0)+" "+         _id
												        	     // Log.d("222222222",cursor2.getString(3));
												        	   //   cont_list=Integer.parseInt(cursor2.getString(2));          //cont
												        	   //   list3.add(cursor2.getString(3));                            //title
												               //   color_src2[key_inni]=Integer.parseInt(cursor2.getString(4));//color
												      	       //   idea2[key_inni]=cursor2.getString(5);    
												      	     
												      	        //  intro.setText(cursor2.getString(6));
												      	       //   hint.setText(cursor2.getString(7));
												      	          //color[key_inni]=  key_inni;
												      	          key_inni++;
												        	     } 
												              }while(cursor2.moveToNext());
												          }       
												    
												        l_manager2.close();
												        cursor2.close();
												        
												     
									        
										    	    }
									      
										    	 
										    	    
										    	    
									        
									         */  
										   //-----------------------------------  
											     
										}
									})
							.setNegativeButton("取消 ",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
									
									
									
										}
									});
					  Window window =  builder.show().getWindow();
					   
		          	  WindowManager.LayoutParams lp = window.getAttributes();  
		        	  lp.alpha = 0.6f;  
	                  window.setAttributes(lp);
	                  
	                  
	                  
	                 // upload_spin.setSelection( upload_spinnerP.size()-1);//let selection to nonchoose 
		        	 
		          } 
					     
		        	 
		        	 
		        	 
		        	 
		         }
		        public void onNothingSelected(AdapterView<?> arg0) {  
		         }  
		     }
		  
		  
		  
		  class Download_SpinnerSelectedListener implements OnItemSelectedListener
		   {   
		     	  
		         public void onItemSelected(AdapterView<?> adapter, View arg1, final int arg2, long arg3) {  
		          
		          if(download_spinnerP.get(arg2).equals("尚未選擇專題"))
		          {}
		          else
		          {
		        	 AlertDialog.Builder builder = new AlertDialog.Builder(homepage2.this);
					  builder.setMessage("確定要下載此專題?")
							.setCancelable(false)
							.setTitle("您所選擇的專題為: "+ download_spinnerP.get(arg2))
							.setPositiveButton("確定",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											showDialog(DIALOG_DOWNLOAD_PROGRESS); 
										    p_name = download_spinnerP_id.get(arg2);
										  
											GetProjectData_Task mAuthTask1 = new GetProjectData_Task();
											   
											mAuthTask1.execute(user.getText().toString(), password.getText().toString(),download_spinnerP_id.get(arg2));//project id
											 
										
										}
									})
							.setNegativeButton("取消 ",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
									
									
									
										}
									});
					  Window window =  builder.show().getWindow();
					   
		          	  WindowManager.LayoutParams lp = window.getAttributes();  
		        	  lp.alpha = 0.6f;  
	                  window.setAttributes(lp);
	                 // upload_spin.setSelection( upload_spinnerP.size()-1);//let selection to nonchoose 
		        	 
		          } 
					     
		        	 
		        	 
		        	 
		        	 
		         }
		        public void onNothingSelected(AdapterView<?> arg0) {  
		         }  
		     }
		  
		  
		  
		  
			private boolean haveInternet()
		    {
		    	boolean result = false;
		    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
		    	NetworkInfo info=connManager.getActiveNetworkInfo();
		    	if (info == null || !info.isConnected())
		    	{
		    		result = false;
		    	}
		    	else 
		    	{
		    		if (!info.isAvailable())
		    		{
		    			result =false;
		    		}
		    		else
		    		{
		    			result = true;
		    		}
		    	}
		    	
		    	return result;
		    }
		  
		  
		  
		  
		  
		  
		  
		  
		  
}
