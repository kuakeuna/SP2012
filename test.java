package com.ea;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class test extends Activity implements SensorEventListener
{
    SensorManager m_sensor_manager;
    Sensor m_accelerometer;

    TextView m_gravity_view;
    TextView m_accel_view;

    DecimalFormat m_format;

    float[] m_gravity_data = new float[3];
    float[] m_accel_data = new float[3];
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
   
        m_format = new DecimalFormat();
    
        m_format.applyLocalizedPattern("0.##");

       
        m_gravity_view = (TextView)findViewById(R.id.acceleration);
        m_accel_view = (TextView)findViewById(R.id.maxAcceleration);

        m_sensor_manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        m_accelerometer = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
        
    protected void onResume()
    {
        super.onResume();
  
        m_sensor_manager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause()
    {
        super.onPause();
       
        m_sensor_manager.unregisterListener(this);
    }
  
    public void onAccuracyChanged(Sensor sensor, int accuracy) 
    {
    }
   
    public void onSensorChanged(SensorEvent event) 
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // 중력 데이터를 구하기 위해서 저속 통과 필터를 적용할 때 사용하는 비율 데이터.
            // t : 저속 통과 필터의 시정수. 시정수란 센서가 가속도의 63% 를 인지하는데 걸리는 시간
            // dT : 이벤트 전송율 혹은 이벤트 전송속도.
            // alpha = t / (t + Dt)
            final float alpha = (float)0.8;
            
            m_gravity_data[0] = alpha * m_gravity_data[0] + (1 - alpha) * event.values[0];
            m_gravity_data[1] = alpha * m_gravity_data[1] + (1 - alpha) * event.values[1];
            m_gravity_data[2] = alpha * m_gravity_data[2] + (1 - alpha) * event.values[2];

            m_accel_data[0] = event.values[0] - m_gravity_data[0];
            m_accel_data[1] = event.values[1] - m_gravity_data[1];
            m_accel_data[2] = event.values[2] - m_gravity_data[2];     
            
            String str;
   
            str = "x : " + m_format.format(m_gravity_data[0]) + ", y : " + m_format.format(m_gravity_data[1]) + 
                                                                      ", z : " + m_format.format(m_gravity_data[2]);
            m_gravity_view.setText(str);
     
            str = "x : " + m_format.format(m_accel_data[0]) + ", y : " + m_format.format(m_accel_data[1]) + 
                                                                      ", z : " + m_format.format(m_accel_data[2]);
            m_accel_view.setText(str);
        }
    }


}
