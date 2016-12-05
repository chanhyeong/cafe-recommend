package com.database.caferecommend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.dial;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.media.CamcorderProfile.get;
import static android.os.Build.VERSION_CODES.N;
import static java.sql.DriverManager.println;

/*
Select
 - select * from 테이블명;
    테이블의 모든 속성과 값을 가져온다.

 - select 속성, 속성... from 테이블명;
    테이블에 선택한 속성의 값만 가져온다.

Insert
 - insert into 테이블명 values(속성, 속성...);
   테이블에 입력한 속성값으로 추가 한다. (속성의 순서가 일치해야 함)

Delete
 - delete from 테이블명 where 조건, 조건...;
   테이블에 조건이 일치하는 값을 삭제한다.

Update
 - update 테이블명 set 값, 값... where 조건, 조건...;
   테이블에 조건에 일치하는 속성의 값을 입력한 값으로 변경한다.

쿼리문에 조건은 콤마(,) 사용 시 여러 조건을 가질 수 있습니다.


 */
public class MainActivity extends AppCompatActivity {
    ArrayList<CafeData>arrData;  //각 카페별 데이터를 저장한다.
    MyAdapter myadapter;         //ListView의 어댑터
    ListView list;               //각 카페데이터를 보여줌
    DBManager dbManager;
    Button plus;
    EditText texxxt;
    Button search;
    int whatSpin;   //  0 = 이름, 1 = 지역, 2 = 특성
    /*
    현재는 public static형태로 다른 class에서 사용이 가능하지만,
    새로운 클래스를 만들어 범용으로 사용하는 것에 대한 정의를 새로 해줄 필요가 있어보임
    */
    public static HashMap<String, Integer> imageNumber = new HashMap<String, Integer>();

    Class c = R.drawable.class;
    Field[] f = c.getFields();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*-- Drawable 폴더의 이미지를 Filename과 int의 HashMap형태로 저장하여 reference를 쉽게함 --*/
        for (Field d : f) {
            try {
                if(d.get("R.drawable." + d.getName()) != null) {
                    imageNumber.put(d.getName(), Integer.valueOf(d.get("R.drawable." + d.getName()).toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager=new DBManager(getApplicationContext(),"cafe",null,1);

        /*
        spinner - 검색할 애들에 대해...
        여기서 어떤 걸 검색할지 값을 받아옴
         */
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                whatSpin = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        texxxt = (EditText) findViewById(R.id.texxxt);

        /*
        검색 버튼.
        검색 버튼을 눌렀을 경우, 해당하는 것들을 가져오도록 하는 문.
         */
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //whatSpin 0- 이름. 1-지역. 2= 특성
                String string = texxxt.getText().toString();

                String part = dbManager.getPart(whatSpin, string);

                //선택한거 보여주기

                arrData=new ArrayList<CafeData>();
                // 코드 확인용 예제문
                // arrData.add(new CafeData(R.mipmap.ic_launcher,"엔젤리너스","010-1111-2222",0));

                try{
                    JSONArray jarray = new JSONArray(part);
                    HashMap<String, String> cafeToImage = new HashMap<String, String>();

                    for(int i=0; i < jarray.length(); i++)
                    {
                        JSONObject jObject = jarray.getJSONObject(i);
                        int cafe_num = jObject.getInt("number");
                        String name = jObject.getString("name");
                        String phone = jObject.getString("phone");
                        String location=jObject.getString("location");
                        int open = jObject.getInt("open");
                        int close = jObject.getInt("close");
                        String address=jObject.getString("address");

                        Log.d("mk",i + ": " + name + phone + cafeToImage.get(name) + imageNumber.get(cafeToImage.get(name)));

                        //이미지  이름     전화번호     주소   지역   오픈시간    마감시간    평균    카페번호
                        if(imageNumber.get(cafeToImage.get(name)) != null)
                            arrData.add(new CafeData(imageNumber.get(cafeToImage.get(name)),location,name,phone,address,open,close,0,cafe_num));
                        else
                            arrData.add(new CafeData(R.mipmap.ic_launcher,name,location,phone,address,open,close,0,cafe_num));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                listMake();
            }
        });//----------------------------------------spinner search end


        plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.custom_alert_layout, null);

         //여기에 dialog에 들어갈 애들 추가
                //customTitle.setTextColor(Color.BLACK);
                ImageView customIcon = (ImageView)view.findViewById(R.id.customdialogicon);

                final EditText dialog_name = (EditText)view.findViewById(R.id.dialog_name);
                final EditText dialog_number = (EditText)view.findViewById(R.id.dialog_number);
                final EditText dialog_open = (EditText)view.findViewById(R.id.dialog_open);
                final EditText dialog_close = (EditText)view.findViewById(R.id.dialog_close);
                final EditText dialog_loc = (EditText)view.findViewById(R.id.dialog_location);
                final EditText dialog_addr = (EditText)view.findViewById(R.id.dialog_address);
                final EditText dialog_char = (EditText)view.findViewById(R.id.dialog_char);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String str_name = dialog_name.getText().toString();
                        String str_number = dialog_number.getText().toString();
                        int str_open = Integer.parseInt(dialog_open.getText().toString());
                        int str_close = Integer.parseInt(dialog_close.getText().toString());
                        String str_loc = dialog_loc.getText().toString();
                        String str_addr = dialog_addr.getText().toString();
                        String str_char = dialog_char.getText().toString();

                        // 추가하는 문장
                        // 옵션 - && str_number != null && str_loc != null && str_addr != null && str_char != null
                        if(str_name != null) // 카페이름을 입력하지 않으면, 추가되지 않도록
                        {
                            String[] values = {str_name, str_number, Integer.toString(str_open), Integer.toString(str_close), str_loc, str_addr, str_char};
                            String query = "CAFE (NAME,PHONE,OPEN_TIME,END_TIME,LOCATE,DETAIL_LOCATE,CATEGORY)" + dbManager.convertString(values);

                            dbManager.insert(query);
                            Log.d("mks...", str_name + str_number);

                            //다시 업로드 하도록 하는 코드 필요!!!!
                            setData();
                            listMake();
                        }
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        setData();
        list=(ListView)findViewById(R.id.list);
        listMake();
    }

    public void listMake()
    {
        myadapter=new MyAdapter(this,arrData);
        list.setAdapter(myadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int push = arrData.get(position).getCafe_num();
                Intent intent=new Intent(MainActivity.this,SubActivity.class);
                intent.putExtra("CafeData",arrData);
                intent.putExtra("value", push);
                startActivity(intent);
            }
        });
    }

    private void setData(){
        String get = dbManager.PrintData("cafe");
        String get2 = dbManager.PrintData("franchise");
        //System.out.println(get);    // for log.

        arrData=new ArrayList<CafeData>();
     // 코드 확인용 예제문
     // arrData.add(new CafeData(R.mipmap.ic_launcher,"엔젤리너스","010-1111-2222",0));

        Log.d("mk", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

        try{
            JSONArray jarray = new JSONArray(get);
            JSONArray jarray2 = new JSONArray(get2);
            HashMap<String, String> cafeToImage = new HashMap<String, String>();

            for(int i=0; i < jarray2.length(); i++) {
                JSONObject jObject2 = jarray2.getJSONObject(i);
                String cafe_name = jObject2.getString("cafe_name");
                String brand_image = jObject2.getString("brand_image");
                cafeToImage.put(cafe_name, brand_image);
                Log.d("mk",i + ": " + cafe_name + brand_image);
            }
            for(int i=0; i < jarray.length(); i++)
            {
                JSONObject jObject = jarray.getJSONObject(i);
                int cafe_num = jObject.getInt("number");
                String name = jObject.getString("name");
                String phone = jObject.getString("phone");
                String location=jObject.getString("location");
                int open = jObject.getInt("open");
                int close = jObject.getInt("close");
                String address=jObject.getString("address");

                Log.d("mk",i + ": " + name + phone + cafeToImage.get(name) + imageNumber.get(cafeToImage.get(name)));

                //이미지  이름     전화번호     주소   지역     오픈시간    마감시간    평균    카페번호
                if(imageNumber.get(cafeToImage.get(name)) != null)
                    arrData.add(new CafeData(imageNumber.get(cafeToImage.get(name)),name,phone,address,location,open,close,4,cafe_num));
                else
                    arrData.add(new CafeData(R.mipmap.ic_launcher,name,phone,address,location,open,close,5,cafe_num));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
