package com.example.dima.beacon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.altbeacon.beacon.*;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class BeaconActivity extends ActionBarActivity implements BeaconConsumer {

    final int szerokoscMapy = 600;
    final int dlugoscMapy = 900;
    final int kratkaRozmiar = 50;
    final int wspolczynnikProporcji = 200;
    final int pozBeacon_x1 = 200;
    final int pozBeacon_y1 = 100;
    final int pozBeacon_x2 = 500;
    final int pozBeacon_y2 = 200;
    final int pozBeacon_x3 = 400;
    final int pozBeacon_y3 = 600;
    final int pozBeacon_x4 = 50;
    final int pozBeacon_y4 = 600;


    public static double[] pozycja(double x1,double y1,double x2,double y2,double x3,double y3, double r1, double r2, double r3) {
		
		double C = r1*r1 - r2*r2 - x1*x1 + x2*x2 - y1*y1 + y2*y2;
		double F = r2*r2 - r3*r3 - x2*x2 + x3*x3 - y2*y2 + y3*y3;
		
		double A = -2*x1 + 2*x2;
		double B = -2*y1 + 2*y2;

		double D = -2*x2 + 2*x3;
		double E = -2*y2 + 2*y3;
		
		double x=0; double y=0;
		
		if((E*A - B*D)!=0 && (B*D - A*E)!=0) {
			x = (C*E - F*B) / (E*A - B*D);
			y = (C*D - A*F) / (B*D - A*E);
		}

        double xy[] = new double[2];

        xy[0]=x;
        xy[1]=y;

        return xy;
    }

    public static final String TAG = "Beacony";
    private BeaconManager beaconManager;

    /**/
    TabHost.TabSpec tabSpec;
    /**/

    //

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                //beaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);

        //beaconManager.setForegroundScanPeriod(50);
        //beaconManager.setAndroidLScanningDisabled(true);
       // beaconManager.setBackgroundBetweenScanPeriod(0);
      //  beaconManager.setBackgroundScanPeriod(1100l);
        //beaconManager.setForegroundScanPeriod(50);
        beaconManager.bind(this);


        /**/
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // inicjalizacja tab
        tabHost.setup();

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Logi");
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Animacje");
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("+");
        tabSpec.setContent(R.id.tab3);
        tabHost.addTab(tabSpec);

        //tabHost.setCurrentTabByTag("tag2");

        /**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d("podl","podlaczamy sie");
        Toast.makeText(BeaconActivity.this, "Podłączamy się!", Toast.LENGTH_LONG).show();

        final Region region = new Region("myBeaons", Identifier.parse("cba44940-2ef8-446f-b0d3-04b794a00530"), null, null);
        //final Region region = new Region("myBeaons", Identifier.parse("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "Wejście do regionu");
                    Toast.makeText(BeaconActivity.this, "Jesteśmy w obszarze naszych UUID", Toast.LENGTH_LONG).show();
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "Wyjście z regionu");
                    Toast.makeText(BeaconActivity.this, "Jesteśmy poza obszarem naszych UUID", Toast.LENGTH_LONG).show();
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

/**/
//ZMIENNE


final ImageView drawingImageView;
drawingImageView = (ImageView) this.findViewById(R.id.DrawingImageView);
Bitmap bitmap = Bitmap.createBitmap(szerokoscMapy, dlugoscMapy, Bitmap.Config.ARGB_8888);

final Canvas canvas = new Canvas(bitmap);
drawingImageView.setImageBitmap(bitmap);

final Paint paint = new Paint();
paint.setColor(Color.BLACK);
paint.setStyle(Paint.Style.STROKE);
paint.setStrokeWidth(3);

final Paint paintSiatka = new Paint();
paintSiatka.setColor(Color.GRAY);
paintSiatka.setStyle(Paint.Style.STROKE);
paintSiatka.setStrokeWidth(1);

final Paint paintCzerwony = new Paint();
paintCzerwony.setColor(Color.RED);
paintCzerwony.setStyle(Paint.Style.STROKE);
paintCzerwony.setStrokeWidth(15);


for(int i = 0;i<=szerokoscMapy;i+=kratkaRozmiar) {
    for (int j = 0; j <= dlugoscMapy; j += kratkaRozmiar) {
        canvas.drawLine(i, 0, i, dlugoscMapy, paintSiatka);
        canvas.drawLine(0, j, szerokoscMapy, j, paintSiatka);
    }
}

//new
        final Paint paintNiebieskiFill = new Paint();
        paintNiebieskiFill.setColor(Color.argb(90,134,199,255));
        paintNiebieskiFill.setStyle(Paint.Style.FILL);

        final Paint paintNiebieskiBorder = new Paint();
        paintNiebieskiBorder.setStyle(Paint.Style.STROKE);
        paintNiebieskiBorder.setStrokeWidth(3);
        paintNiebieskiBorder.setColor(Color.rgb(70,104,242));
        //
        final Paint paintZielonyFill = new Paint();
        paintZielonyFill.setColor(Color.argb(50,55,165,62));
        paintZielonyFill.setStyle(Paint.Style.FILL);

        final Paint paintZielonyBorder = new Paint();
        paintZielonyBorder.setStyle(Paint.Style.STROKE);
        paintZielonyBorder.setStrokeWidth(3);
        paintZielonyBorder.setColor(Color.rgb(53,144,59));
        //
        final Paint paintCzerwonyFill = new Paint();
        paintCzerwonyFill.setColor(Color.argb(50,242,73,73));
        paintCzerwonyFill.setStyle(Paint.Style.FILL);

        final Paint paintCzerwonyBorder = new Paint();
        paintCzerwonyBorder.setStyle(Paint.Style.STROKE);
        paintCzerwonyBorder.setStrokeWidth(3);
        paintCzerwonyBorder.setColor(Color.rgb(230,33,33));
        //

        final Path pathPolygon_1 = new Path();
        pathPolygon_1.reset(); // only needed when reusing this path for a new build
        pathPolygon_1.moveTo(10, 20); // used for first point
        pathPolygon_1.lineTo(10, 300);
        pathPolygon_1.lineTo(200, 300);
        pathPolygon_1.lineTo(200, 10);
        pathPolygon_1.lineTo(150, 10);
        pathPolygon_1.lineTo(150, 20);
        pathPolygon_1.close();
        canvas.drawPath(pathPolygon_1, paintNiebieskiFill);
        canvas.drawPath(pathPolygon_1, paintNiebieskiBorder);
        //
        final Path pathPolygon_2 = new Path();
        pathPolygon_2.reset(); // only needed when reusing this path for a new build
        pathPolygon_2.moveTo(210, 10); // used for first point
        pathPolygon_2.lineTo(210, 350);
        pathPolygon_2.lineTo(250, 350);
        pathPolygon_2.lineTo(250, 290);
        pathPolygon_2.lineTo(400, 290);
        pathPolygon_2.lineTo(400, 330);
        pathPolygon_2.lineTo(430, 330);
        pathPolygon_2.lineTo(430, 230);
        pathPolygon_2.lineTo(330, 230);
        pathPolygon_2.lineTo(330, 190);
        pathPolygon_2.lineTo(430, 190);
        pathPolygon_2.lineTo(430, 10);
        pathPolygon_2.close();
        canvas.drawPath(pathPolygon_2, paintNiebieskiFill);
        canvas.drawPath(pathPolygon_2, paintNiebieskiBorder);
        //
        final Path pathPolygon_3 = new Path();
        pathPolygon_3.reset(); // only needed when reusing this path for a new build
        pathPolygon_3.moveTo(10, 310); // used for first point
        pathPolygon_3.lineTo(200, 310);
        pathPolygon_3.lineTo(200, 400);
        pathPolygon_3.lineTo(500, 400);
        pathPolygon_3.lineTo(500, 600);
        pathPolygon_3.lineTo(10, 600);
        pathPolygon_3.close();
        canvas.drawPath(pathPolygon_3, paintZielonyFill);
        canvas.drawPath(pathPolygon_3, paintZielonyBorder);
        //
        final Path pathPolygon_4 = new Path();
        pathPolygon_4.reset(); // only needed when reusing this path for a new build
        pathPolygon_4.moveTo(440, 10); // used for first point
        pathPolygon_4.lineTo(590, 10);
        pathPolygon_4.lineTo(590, 800);
        pathPolygon_4.lineTo(510, 800);
        pathPolygon_4.lineTo(510, 390);
        pathPolygon_4.lineTo(440, 390);
        pathPolygon_4.close();
        canvas.drawPath(pathPolygon_4, paintZielonyFill);
        canvas.drawPath(pathPolygon_4, paintZielonyBorder);
        //
        final Path pathPolygon_5 = new Path();
        pathPolygon_5.reset(); // only needed when reusing this path for a new build
        pathPolygon_5.moveTo(10, 610); // used for first point
        pathPolygon_5.lineTo(500, 610);
        pathPolygon_5.lineTo(500, 810);
        pathPolygon_5.lineTo(590, 810);
        pathPolygon_5.lineTo(590, 890);
        pathPolygon_5.lineTo(10, 890);
        pathPolygon_5.close();
        canvas.drawPath(pathPolygon_5, paintCzerwonyFill);
        canvas.drawPath(pathPolygon_5, paintCzerwonyBorder);
        //

        final Drawable d = getResources().getDrawable(R.drawable.mybeacon);
        d.setBounds(20,50,80,120);
        d.draw(canvas);
        d.setBounds(120,230,180,300);
        d.draw(canvas);
        d.setBounds(320,230,380,300);
        d.draw(canvas);
        d.setBounds(20,620,80,690);
        d.draw(canvas);
        d.setBounds(400,520,460,590);
        d.draw(canvas);
        //
        final Drawable door = getResources().getDrawable(R.drawable.door);
        door.setBounds(40,270,100,340);
        door.draw(canvas);
        door.setBounds(170,140,230,200);
        door.draw(canvas);
        door.setBounds(400,90,460,150);
        door.draw(canvas);
        door.setBounds(400,90,460,150);
        door.draw(canvas);
        door.setBounds(300,270,360,340);
        door.draw(canvas);
        door.setBounds(330,370,390,440);
        door.draw(canvas);
        door.setBounds(230,570,290,640);
        door.draw(canvas);
        door.setBounds(480,520,540,590);
        door.draw(canvas);
        //
        final Drawable comp = getResources().getDrawable(R.drawable.myicon1);
        comp.setBounds(20,800,80,870);
        comp.draw(canvas);
        comp.setBounds(520,20,580,90);
        comp.draw(canvas);
        //
        canvas.drawCircle(50,85,300,paint);
        canvas.drawCircle(150,265,200,paint);
        canvas.drawCircle(350,265,75,paint);
        canvas.drawCircle(50,655,505,paint);
        canvas.drawCircle(430,555,355,paint);
        //


//new

//canvas.drawCircle(50,50,15,paintCzerwony);
//canvas.drawCircle(x, y, radius, paint);


/**/

        final EditText editText1 = (EditText)findViewById(R.id.editText);
        final EditText editTextCopy = (EditText)findViewById(R.id.editTextCopy);

        editText1.setEnabled(false);
        editText1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,0);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            public int numerBeaconu = 0;

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText1.setText("");
                        editTextCopy.setText("");
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        canvas.drawColor(Color.parseColor("#DCF3FC"));
                        drawingImageView.invalidate();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i<=szerokoscMapy;i+=kratkaRozmiar) {
                            for (int j = 0; j <= dlugoscMapy; j += kratkaRozmiar) {
                                canvas.drawLine(i, 0, i, dlugoscMapy, paintSiatka);
                                canvas.drawLine(0, j, szerokoscMapy, j, paintSiatka);
                            }
                        }
                        //
                        canvas.drawPath(pathPolygon_1, paintNiebieskiFill);
                        canvas.drawPath(pathPolygon_1, paintNiebieskiBorder);
                        canvas.drawPath(pathPolygon_2, paintNiebieskiFill);
                        canvas.drawPath(pathPolygon_2, paintNiebieskiBorder);
                        canvas.drawPath(pathPolygon_3, paintZielonyFill);
                        canvas.drawPath(pathPolygon_3, paintZielonyFill);
                        canvas.drawPath(pathPolygon_4, paintZielonyFill);
                        canvas.drawPath(pathPolygon_4, paintZielonyFill);
                        canvas.drawPath(pathPolygon_5, paintCzerwonyFill);
                        canvas.drawPath(pathPolygon_5, paintCzerwonyBorder);
                        door.setBounds(40,270,100,340);
                        door.draw(canvas);
                        door.setBounds(170,140,230,200);
                        door.draw(canvas);
                        door.setBounds(400,90,460,150);
                        door.draw(canvas);
                        door.setBounds(400,90,460,150);
                        door.draw(canvas);
                        door.setBounds(300,270,360,340);
                        door.draw(canvas);
                        door.setBounds(330,370,390,440);
                        door.draw(canvas);
                        door.setBounds(230,570,290,640);
                        door.draw(canvas);
                        door.setBounds(480,520,540,590);
                        door.draw(canvas);
                        comp.draw(canvas);
                        d.setBounds(20,545,80,645);
                        d.draw(canvas);
                    }
                });

                final List<Double> list = new ArrayList<Double>();
                list.clear();
                list.add((double)0.01);
                list.add((double)0.01);
                list.add((double)0.01);
                numerBeaconu=0;

                for (final Beacon oneBeacon : beacons) {

					runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
						   if(oneBeacon.getId3().toInt() == 1) {
							   list.set(0,(double)oneBeacon.getDistance());
						   }
						   if(oneBeacon.getId3().toInt() == 2) {
							   list.set(1,(double)oneBeacon.getDistance());
						   }
						   if(oneBeacon.getId3().toInt() == 3) {
							   list.set(2,(double)oneBeacon.getDistance());
						   } 						   
                       }
                   });

                    Log.d("beacons","znaleziono beacony");
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           //editText1.append("" + oneBeacon.getDistance() + "");

                            editTextCopy.append("odległość: " + oneBeacon.getDistance() + "m\nid:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3() + "\nrssi: " + oneBeacon.getRssi() + "\ntxPower: " + oneBeacon.getTxPower() + "\n\n" );

                            // + (int) getWindowManager() .getDefaultDisplay().getWidth() + " " + (int) getWindowManager() .getDefaultDisplay().getHeight()

                        }
                    });




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(oneBeacon.getId3().toInt()==1) {
                                canvas.drawCircle(pozBeacon_x1, pozBeacon_y1, (float)oneBeacon.getDistance()*wspolczynnikProporcji, paint);
                                canvas.drawCircle(pozBeacon_x1,pozBeacon_y1,5,paint);
                            }
                        }
                    });
                    //4
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(oneBeacon.getId3().toInt()==4) {
                                canvas.drawCircle(pozBeacon_x4, pozBeacon_y4, (float)oneBeacon.getDistance()*wspolczynnikProporcji, paint);
                                canvas.drawCircle(pozBeacon_x4,pozBeacon_y4,5,paint);
                            }
                        }
                    });
                    //


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(oneBeacon.getId3().toInt()==2) {
                                canvas.drawCircle(pozBeacon_x2, pozBeacon_y2, (float)oneBeacon.getDistance()*wspolczynnikProporcji, paint);
                                canvas.drawCircle(pozBeacon_x2,pozBeacon_y2,5,paint);
                            }
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(oneBeacon.getId3().toInt()==3) {
                                canvas.drawCircle(pozBeacon_x3, pozBeacon_y3, (float)oneBeacon.getDistance()*wspolczynnikProporcji, paint);
                                canvas.drawCircle(pozBeacon_x3,pozBeacon_y3,5,paint);
                            }
                        }
                    });


                }//foreach

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final double[] a = pozycja(pozBeacon_x1,pozBeacon_y1,pozBeacon_x2,pozBeacon_y2,pozBeacon_x3,pozBeacon_y3,list.get(0)*wspolczynnikProporcji,list.get(1)*wspolczynnikProporcji,list.get(2)*wspolczynnikProporcji);
                        //editText1.append("Twoja pozycja");

                        if(list.get(0)!=0.01 && list.get(1)!=0.01 && list.get(2)==0.01) {//jesli wszystkie sa znalezione
                            editTextCopy.append("x:" + a[0] + " y:" + a[1] + "\n");
                        }

                        if(list.get(0)!=0.01 && list.get(1)!=0.01 && list.get(2)==0.01) {
                            canvas.drawCircle((float) a[0], (float) a[1], 4, paintCzerwony);
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(Double promien : list){
                            editText1.append(""+promien+" | ");

                            if(list.get(0)!=0.01 && list.get(1)!=0.01 && list.get(2)==0.01) {
                                editTextCopy.append("" + promien + " | ");
                            }
                        }
                    }
                });

            }//rangenotifier
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}