package org.ctavkep.tirecalculator;

import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.ctavkep.tirecalculator.Tire.SPEED_INDEX;

public class TireActivity extends AppCompatActivity {
    private static final int MIN_TIRE_WIDTH = 125;
    private static final int MIN_ASPECT_RATION = 20;
    private static final int MIN_RIM_DIAMETER = 10;
    private static final int MIN_LOAD_INDEX = 40;

    private static final int TIRE_WIDTH_CAPACITY = 24;
    private static final int ASPECT_RATION_CAPACITY = 15;
    private static final int RIM_DIAMETER_CAPACITY = 15;
    private static final int LOAD_INDEX_CAPACITY = 91;

    private final String LOG_TAG = "TireActivity";

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private static final String FILE_NAME = "tire_parameters.txt";

    private Tire mCurrentTire;
    private Tire mNewTire;

    private ArrayAdapter<String> adapter;

    private TextView mCurrentSidewallHeight, mCurrentRimWidth, mCurrentOverallDiameter,
            mCurrentDiameter, mCurrentCircumference, mCurrentRpkm, mCurrentRpm,
            mCurrentContactArea, mCurrentMaxLoad, mCurrentMaxSpeed;

    private TextView mNewSidewallHeight, mNewRimWidth, mNewOverallDiameter, mNewDiameter,
            mNewCircumference, mNewRpkm, mNewRpm, mNewContactArea, mNewMaxLoad, mNewMaxSpeed;

    private TextView mDifferSidewallHeight, mDifferRimWidth, mDifferOverallDiameter,
            mDifferDiameter, mDifferCircumference, mDifferRpkm, mDifferRpm, mDifferContactArea,
            mDifferMaxLoad, mDifferMaxSpeed, mDifferClearance;
    private Spinner mCurrentSpeedIndexSpinner, mNewSpeedIndexSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire);

        mCurrentTire = new Tire();
        mNewTire = new Tire();

        initTabHost();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu:
                FragmentManager manager = getFragmentManager();
                InfoDialog dialog = new InfoDialog();
                dialog.show(manager, null);
                return true;
            case R.id.save_menu:
                saveTireParameters();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeDataToFile();
                } else {
                    Log.i(LOG_TAG, "Failed to save data");
                    Toast.makeText(TireActivity.this, getString(R.string.save_permission_denied_toast),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equals("tab2")) {
                    updateCurrentParameters();
                    updateNewParameters();
                    updateTireDifference();
                }
            }
        });
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1");

        tabSpec.setContent(R.id.tire_size);
        tabSpec.setIndicator(getString(R.string.tab1_title));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setContent(R.id.tire_results);
        tabSpec.setIndicator(getString(R.string.tab2_title));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    private void initViews() {
        createSpinnerAdapter(MIN_TIRE_WIDTH, 10, TIRE_WIDTH_CAPACITY);

        Spinner currentTireWidthSpinner = (Spinner) findViewById(R.id.old_tire_width);
        currentTireWidthSpinner.setAdapter(adapter);
        currentTireWidthSpinner.setSelection(8);
        currentTireWidthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mCurrentTire.setTireWidth(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner newTireWidthSpinner = (Spinner) findViewById(R.id.new_tire_width);
        newTireWidthSpinner.setAdapter(adapter);
        newTireWidthSpinner.setSelection(10);
        newTireWidthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mNewTire.setTireWidth(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createSpinnerAdapter(MIN_ASPECT_RATION, 5, ASPECT_RATION_CAPACITY);

        Spinner currentAspectRationSpinner = (Spinner) findViewById(R.id.old_aspect_ration);
        currentAspectRationSpinner.setAdapter(adapter);
        currentAspectRationSpinner.setSelection(7);
        currentAspectRationSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mCurrentTire.setAspectRation(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner newAspectRationSpinner = (Spinner) findViewById(R.id.new_aspect_ration);
        newAspectRationSpinner.setAdapter(adapter);
        newAspectRationSpinner.setSelection(5);
        newAspectRationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mNewTire.setAspectRation(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createSpinnerAdapter(MIN_RIM_DIAMETER, 1, RIM_DIAMETER_CAPACITY);

        Spinner currentRimDiameterSpinner = (Spinner) findViewById(R.id.old_rim_diameter);
        currentRimDiameterSpinner.setAdapter(adapter);
        currentRimDiameterSpinner.setSelection(6);
        currentRimDiameterSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mCurrentTire.setRimDiameter(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner newRimDiameterSpinner = (Spinner) findViewById(R.id.new_rim_diameter);
        newRimDiameterSpinner.setAdapter(adapter);
        newRimDiameterSpinner.setSelection(7);
        newRimDiameterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mNewTire.setRimDiameter(currentItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createSpinnerAdapter(MIN_LOAD_INDEX, 1, LOAD_INDEX_CAPACITY);

        Spinner currentLoadIndexSpinner = (Spinner) findViewById(R.id.old_load_index);
        currentLoadIndexSpinner.setAdapter(adapter);
        currentLoadIndexSpinner.setSelection(50);
        currentLoadIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mCurrentTire.setLoadIndex(currentItem);
                mCurrentTire.setMaxLoad(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner newLoadIndexSpinner = (Spinner) findViewById(R.id.new_load_index);
        newLoadIndexSpinner.setAdapter(adapter);
        newLoadIndexSpinner.setSelection(51);
        newLoadIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentItem = Integer.parseInt(adapterView.getSelectedItem().toString());
                mNewTire.setLoadIndex(currentItem);
                mNewTire.setMaxLoad(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, SPEED_INDEX);

        mCurrentSpeedIndexSpinner = (Spinner) findViewById(R.id.old_speed_index);
        mCurrentSpeedIndexSpinner.setAdapter(adapter);
        mCurrentSpeedIndexSpinner.setSelection(14);
        mCurrentSpeedIndexSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentTire.setSpeedIndex(adapterView.getSelectedItem().toString());
                mCurrentTire.setMaxSpeed(i);

                if (adapterView.getSelectedItem().toString().equals("ZR")) {
                    mCurrentMaxSpeed.setText(String.format(Locale.ENGLISH, ">%d",
                            mCurrentTire.getMaxSpeed()));
                    mDifferMaxSpeed.setText(R.string.n_a);
                } else {
                    mCurrentMaxSpeed.setText(String.format(Locale.ENGLISH, "%d",
                            mCurrentTire.getMaxSpeed()));
                    if (!mNewSpeedIndexSpinner.getSelectedItem().equals("ZR")) {
                        int speedDiffer = mNewTire.getMaxSpeed() - mCurrentTire.getMaxSpeed();
                        mDifferMaxSpeed.setText(String.format(Locale.ENGLISH, "% d", speedDiffer));
                        setValueColor((double) speedDiffer, mDifferMaxSpeed);
                    } else {
                        mDifferMaxSpeed.setText(R.string.n_a);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mNewSpeedIndexSpinner = (Spinner) findViewById(R.id.new_speed_index);
        mNewSpeedIndexSpinner.setAdapter(adapter);
        mNewSpeedIndexSpinner.setSelection(20);
        mNewSpeedIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mNewTire.setSpeedIndex(adapterView.getSelectedItem().toString());
                mNewTire.setMaxSpeed(i);

                if (adapterView.getSelectedItem().equals("ZR")) {
                    mNewMaxSpeed.setText(String.format(Locale.ENGLISH, ">%d",
                            mNewTire.getMaxSpeed()));
                    mDifferMaxSpeed.setText(R.string.n_a);
                } else {
                    mNewMaxSpeed.setText(String.format(Locale.ENGLISH, "%d",
                            mNewTire.getMaxSpeed()));
                    if (!mCurrentSpeedIndexSpinner.getSelectedItem().equals("ZR")) {
                        int speedDiffer = mNewTire.getMaxSpeed() - mCurrentTire.getMaxSpeed();
                        mDifferMaxSpeed.setText(String.format(Locale.ENGLISH, "% d", speedDiffer));
                        setValueColor((double) speedDiffer, mDifferMaxSpeed);
                    } else {
                        mDifferMaxSpeed.setText(R.string.n_a);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCurrentSidewallHeight = (TextView) findViewById(R.id.old_sidewall_height);
        mCurrentRimWidth = (TextView) findViewById(R.id.old_rim_width);
        mCurrentOverallDiameter = (TextView) findViewById(R.id.old_overall_diameter);
        mCurrentDiameter = (TextView) findViewById(R.id.old_diameter);
        mCurrentCircumference = (TextView) findViewById(R.id.old_circumference);
        mCurrentRpkm = (TextView) findViewById(R.id.old_rpkm);
        mCurrentRpm = (TextView) findViewById(R.id.old_rpm);
        mCurrentContactArea = (TextView) findViewById(R.id.old_contact_area);
        mCurrentMaxLoad = (TextView) findViewById(R.id.old_max_load);
        mCurrentMaxSpeed = (TextView) findViewById(R.id.old_max_speed);

        mNewSidewallHeight = (TextView) findViewById(R.id.new_sidewall_height);
        mNewRimWidth = (TextView) findViewById(R.id.new_rim_width);
        mNewOverallDiameter = (TextView) findViewById(R.id.new_overall_diameter);
        mNewDiameter = (TextView) findViewById(R.id.new_diameter);
        mNewCircumference = (TextView) findViewById(R.id.new_circumference);
        mNewRpkm = (TextView) findViewById(R.id.new_rpkm);
        mNewRpm = (TextView) findViewById(R.id.new_rpm);
        mNewContactArea = (TextView) findViewById(R.id.new_contact_area);
        mNewMaxLoad = (TextView) findViewById(R.id.new_max_load);
        mNewMaxSpeed = (TextView) findViewById(R.id.new_max_speed);

        mDifferSidewallHeight = (TextView) findViewById(R.id.differ_sidewall_height);
        mDifferRimWidth = (TextView) findViewById(R.id.differ_rim_width);
        mDifferOverallDiameter = (TextView) findViewById(R.id.differ_overall_diameter);
        mDifferDiameter = (TextView) findViewById(R.id.differ_rim_diameter);
        mDifferCircumference = (TextView) findViewById(R.id.differ_circumference);
        mDifferRpkm = (TextView) findViewById(R.id.differ_rpkm);
        mDifferRpm = (TextView) findViewById(R.id.differ_rpm);
        mDifferContactArea = (TextView) findViewById(R.id.differ_contact_area);
        mDifferMaxLoad = (TextView) findViewById(R.id.differ_max_load);
        mDifferMaxSpeed = (TextView) findViewById(R.id.differ_max_speed);
        mDifferClearance = (TextView) findViewById(R.id.differ_clearance);
    }

    private void saveTireParameters() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_results_dialog);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int permission = ContextCompat.checkSelfPermission(TireActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TireActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
                    return;
                }
                writeDataToFile();
            }
        }).setNegativeButton(android.R.string.no, null);
        builder.create();
        builder.show();
    }

    private void writeDataToFile() {
        double[] currentParameters = mCurrentTire.getParameters();
        double[] newParameters = mNewTire.getParameters();
        double[] difference = mNewTire.compareTo(mCurrentTire);

        final String parameters = String.format(Locale.ENGLISH, "%25s%-18s%-18s%s\n\n%s\n\n", "",
                mCurrentTire.getSidewallLabel(), mNewTire.getSidewallLabel(),
                getString(R.string.differ_label),
                String.format(Locale.ENGLISH, "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18.2f%-18.2f% .2f\n"
                                + "%-25s%-18d%-18d% d\n"
                                + "%-25s%-18d%-18d%s\n"
                                + "%-25s%-18s%42$-18s% .2f",
                        getString(R.string.sidewall_height), currentParameters[0],
                        newParameters[0], difference[0],
                        getString(R.string.rim_width), currentParameters[1], newParameters[1],
                        difference[1],
                        getString(R.string.overall_diameter), currentParameters[2],
                        newParameters[2], difference[2],
                        getString(R.string.rim_diameter), currentParameters[3], newParameters[3],
                        difference[3],
                        getString(R.string.circumference), currentParameters[4], newParameters[4],
                        difference[4],
                        getString(R.string.revs_per_km), currentParameters[5], newParameters[5],
                        difference[5],
                        getString(R.string.rpm_at_100), currentParameters[6], newParameters[6],
                        difference[6],
                        getString(R.string.contact_area), currentParameters[7], newParameters[7],
                        difference[7],
                        getString(R.string.max_load), mCurrentTire.getMaxLoad(),
                        mNewTire.getMaxLoad(), mNewTire.getMaxLoad() - mCurrentTire.getMaxLoad(),
                        getString(R.string.max_speed), mCurrentTire.getMaxSpeed(),
                        mNewTire.getMaxSpeed(), mDifferMaxSpeed.getText(),
                        getString(R.string.clearance), getString(R.string.n_a), difference[2] / 2));

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, FILE_NAME);
        if (!path.exists() && !path.mkdirs()) {
            Log.w(LOG_TAG, "Failed to make " + path);
        } else {
            try (FileOutputStream fileStream = new FileOutputStream(file, true)) {
                fileStream.write(parameters.getBytes());
                Toast.makeText(TireActivity.this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.w(LOG_TAG, "Error writing " + file, e);
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createSpinnerAdapter(int minValue, int step, int capacity) {
        String[] array = new String[capacity];

        for (int i = 0; i < capacity; i++) {
            array[i] = Integer.toString(minValue);
            minValue += step;
        }
        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, array);
    }

    private void updateCurrentParameters() {
        double[] parameters = mCurrentTire.getParameters();

        mCurrentSidewallHeight.setText(String.format(Locale.ENGLISH, "%.2f", parameters[0]));
        mCurrentRimWidth.setText(String.format(Locale.ENGLISH, "%.2f", parameters[1]));
        mCurrentOverallDiameter.setText(String.format(Locale.ENGLISH, "%.2f", parameters[2]));
        mCurrentDiameter.setText(String.format(Locale.ENGLISH, "%.2f", parameters[3]));
        mCurrentCircumference.setText(String.format(Locale.ENGLISH, "%.2f", parameters[4]));
        mCurrentRpkm.setText(String.format(Locale.ENGLISH, "%.2f", parameters[5]));
        mCurrentRpm.setText(String.format(Locale.ENGLISH, "%.2f", parameters[6]));
        mCurrentContactArea.setText(String.format(Locale.ENGLISH, "%.2f", parameters[7]));
        mCurrentMaxLoad.setText(String.format(Locale.ENGLISH, "%s", mCurrentTire.getMaxLoad()));
    }

    private void updateNewParameters() {
        double[] parameters = mNewTire.getParameters();

        mNewSidewallHeight.setText(String.format(Locale.ENGLISH, "%.2f", parameters[0]));
        mNewRimWidth.setText(String.format(Locale.ENGLISH, "%.2f", parameters[1]));
        mNewOverallDiameter.setText(String.format(Locale.ENGLISH, "%.2f", parameters[2]));
        mNewDiameter.setText(String.format(Locale.ENGLISH, "%.2f", parameters[3]));
        mNewCircumference.setText(String.format(Locale.ENGLISH, "%.2f", parameters[4]));
        mNewRpkm.setText(String.format(Locale.ENGLISH, "%.2f", parameters[5]));
        mNewRpm.setText(String.format(Locale.ENGLISH, "%.2f", parameters[6]));
        mNewContactArea.setText(String.format(Locale.ENGLISH, "%.2f", parameters[7]));
        mNewMaxLoad.setText(String.format(Locale.ENGLISH, "%s", mNewTire.getMaxLoad()));
    }

    private void updateTireDifference() {
        double[] difference = mNewTire.compareTo(mCurrentTire);

        mDifferSidewallHeight.setText(String.format(Locale.ENGLISH, "% .2f", difference[0]));
        setValueColor(difference[0], mDifferSidewallHeight);

        mDifferRimWidth.setText(String.format(Locale.ENGLISH, "% .2f", difference[1]));
        setValueColor(difference[1], mDifferRimWidth);

        mDifferOverallDiameter.setText(String.format(Locale.ENGLISH, "% .2f", difference[2]));
        setValueColor(difference[2], mDifferOverallDiameter);

        mDifferDiameter.setText(String.format(Locale.ENGLISH, "% .2f", difference[3]));
        setValueColor(difference[3], mDifferDiameter);

        mDifferCircumference.setText(String.format(Locale.ENGLISH, "% .2f", difference[4]));
        setValueColor(difference[4], mDifferCircumference);

        mDifferRpkm.setText(String.format(Locale.ENGLISH, "% .2f", difference[5]));
        setValueColor(difference[5], mDifferRpkm);

        mDifferRpm.setText(String.format(Locale.ENGLISH, "% .2f", difference[6]));
        setValueColor(difference[6], mDifferRpm);

        mDifferContactArea.setText(String.format(Locale.ENGLISH, "% .2f", difference[7]));
        setValueColor(difference[7], mDifferContactArea);

        int loadDiffer = mNewTire.getMaxLoad() - mCurrentTire.getMaxLoad();
        mDifferMaxLoad.setText(String.format(Locale.ENGLISH, "% d", loadDiffer));
        setValueColor((double) loadDiffer, mDifferMaxLoad);

        mDifferClearance.setText(String.format(Locale.ENGLISH, "% .2f", difference[2] / 2));
        setValueColor(difference[2] / 2, mDifferClearance);
    }

    private void setValueColor(double value, TextView textView) {
        if (value >= 0)
            textView.setTextColor(0xff008000);
        else
            textView.setTextColor(0xffFF0000);
    }
}