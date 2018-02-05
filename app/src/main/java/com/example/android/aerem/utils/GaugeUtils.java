package com.example.android.aerem.utils;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;

public class GaugeUtils {

    private static final String PM25 = "pm25", PM10 = "pm10", SO2 = "so2", NO2 = "no2",
            O3 = "o3", CO = "co", BC = "bc";
    private static final String MICROGRAMS_CUBIC_METER = "µg/m³", PARTS_PER_MILLION = "ppm";
    private static final Float PM25_MCM_HIGH = 100.00f, PM25_PPM_HIGH = 150.00f;
    private static final Float PM10_MCM_HIGH = 60.00f, PM10_PPM_HIGH = 60.00f;
    private static final Float SO2_MCM_HIGH = 50.00f, SO2_PPM_HIGH = 50.00f;
    private static final Float NO2_MCM_HIGH = 150.00f, NO2_PPM_HIGH = 50.00f;
    private static final Float O3_MCM_HIGH = 140.00f, O3_PPM_HIGH = 60.00f;
    private static final Float CO_MCM_HIGH = 10000.00f, CO_PPM_HIGH = 30.00f;
    private static final Float BC_MCM_HIGH = 40.00f, BC_PPM_HIGH = 40.00f;
    private static   Float START_ANGLE = -155.0f;

    private static float needleAngleForPollutant(String type, String unit, String result) {
        float angle ;

        switch (unit) {
            case MICROGRAMS_CUBIC_METER:
                switch (type) {
                    case PM25:
                        angle = angleFor(result, PM25_MCM_HIGH);
                        break;
                    case PM10:
                        angle = angleFor(result, PM10_MCM_HIGH);
                        break;
                    case SO2:
                        angle = angleFor(result, SO2_MCM_HIGH);
                        break;
                    case NO2:
                        angle = angleFor(result, NO2_MCM_HIGH);
                        break;
                    case O3:
                        angle = angleFor(result, O3_MCM_HIGH);
                        break;
                    case CO:
                        angle = angleFor(result, CO_MCM_HIGH);
                        break;
                    case BC:
                        angle = angleFor(result, BC_MCM_HIGH);
                        break;
                    default:
                        angle = 0.0f;
                        break;
                }
                break;
            case PARTS_PER_MILLION:
                switch (type) {
                    case PM25:
                        angle = angleFor(result, PM25_PPM_HIGH);
                        break;
                    case PM10:
                        angle = angleFor(result, PM10_PPM_HIGH);
                        break;
                    case SO2:
                        angle = angleFor(result, SO2_PPM_HIGH);
                        break;
                    case NO2:
                        angle = angleFor(result, NO2_PPM_HIGH);
                        break;
                    case O3:
                        angle = angleFor(result, O3_PPM_HIGH);
                        break;
                    case CO:
                        angle = angleFor(result, CO_PPM_HIGH);
                        break;
                    case BC:
                        angle = angleFor(result, BC_PPM_HIGH);
                        break;
                    default:
                        angle = 0.0f;
                        break;
                }
                break;
            default:
                angle = 0.0f;
                break;
        }
        return angle;
    }

    private static float angleFor(String result, Float highLevel) {
        Float r = Float.parseFloat(result);
        if (highLevel >= 10000.00f) {
            highLevel = highLevel / 1000.00f;
            r = r / 1000.00f;
        }
        if (r <= 0.0f) {
            r = 0.0f;
        }
        if (r >= highLevel) {
            r = highLevel;
        }
        return (r / highLevel * 310f) - (310 / 2);
    }

    public static void gaugePointerForMeasurement(View view, String type, String unit, String result) {
   Float angle = needleAngleForPollutant(type, unit, result);
        RotateAnimation animation =
                new RotateAnimation(
                        START_ANGLE,
                        angle,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.84f);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(2000);
       view.startAnimation(animation);
        START_ANGLE = angle;
    }
}

