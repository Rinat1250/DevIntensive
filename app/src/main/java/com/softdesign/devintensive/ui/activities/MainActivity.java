package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.PhotoBitmap;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private int mCurrenEditMode=0;


    private ImageView avatar;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDraver;
    private NavigationView mNavigationView;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout mAppBarLayout;
    private ImageView mProfileImage;

    private ImageView mCallImg;
    private ImageView mEmailImg;
    private ImageView mVkImg;
    private ImageView mGitImg;



    private EditText mUserPhone,mUserMail, mUserVk, mUserGit, mUserBio;

    private List<EditText> mUserInfoViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,("onCreate"));

        mDataManager = DataManager.getInstance();


        mCoordinatorLayout=(CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar=(Toolbar) findViewById(R.id.toolbar);
        mNavigationDraver = (DrawerLayout) findViewById(R.id.navigation_draw);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVk = (EditText) findViewById(R.id.profil_vk_et);
        mUserGit = (EditText) findViewById(R.id.git_et);
        mUserBio = (EditText) findViewById(R.id.bio_et);

        mCallImg = (ImageView)findViewById(R.id.call_img);
        mEmailImg = (ImageView)findViewById(R.id.email_img);
        mVkImg = (ImageView)findViewById(R.id.vk_img);
        mGitImg = (ImageView)findViewById(R.id.git_img);

        Bitmap _avatar = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        View headerLayout = mNavigationView.getHeaderView(0);
        avatar = (ImageView) headerLayout.findViewById(R.id.avatar);
        avatar.setImageBitmap(PhotoBitmap.getPhotoBitmap(_avatar));

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolBar();
        setupDrawer();
        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferenceManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo) //// TODO: 01.07.2016 сделать плейхолдер и трансформ + crop
                .into(mProfileImage);


        if (savedInstanceState == null) {
            //активити запускается впервые
        }else {
            mCurrenEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrenEditMode);

        }

            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            mNavigationDraver.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDraver.isDrawerOpen(GravityCompat.START))
            mNavigationDraver.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,("onStart"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,("onResume"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,("onPause"));
        saveUserInfoValue();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,("onStop"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,("onDestroy"));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,("onRestart"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:

                if (mCurrenEditMode==0) {
                    changeEditMode(1);
                    mCurrenEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrenEditMode = 0;
                }

                break;

            case R.id.profile_placeholder:
                //// TODO: 30.06.2016  сделать выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_img:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserPhone.getText().toString()));
                startActivity(dialIntent);
                break;

            case R.id.email_img:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Content text");
                shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mUserMail.getText().toString() });
                startActivity(Intent.createChooser(shareIntent, "Sharing something."));
                break;

            case R.id.vk_img:
                Intent browseIntentVk = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserVk.getText().toString()));
                startActivity(browseIntentVk);
                break;

            case R.id.git_img:
                Intent browseIntentGit = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserGit.getText().toString()));
                startActivity(browseIntentGit);
                break;

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrenEditMode);

     }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void setupToolBar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if(actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    private void setupDrawer(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDraver.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        }

    /**
     * Получение результата из другой Activity (фото из камеры или галерии)
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data !=null ) {
                mSelectedImage = data.getData();

                insertProfileImage(mSelectedImage);
            }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile !=null ) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }

                }
    }



    /**
     * переключает режим редактирования
     * @param mode ксли true режим редактирования, если falce режим просмотра
     */

    private void changeEditMode (int mode) {
        if (mode == 1){

            for (EditText userValue : mUserInfoViews) {
                mFab.setImageResource(R.drawable.ic_done_black_24dp);
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }

        }else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserInfoValue();

        }
    }
    }

    private void loadUserInfoValue(){
        List<String> userData = mDataManager.getPreferenceManager().loadUserProfileData();
        for (int i=0; i<userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }

    }

    private void saveUserInfoValue(){
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView :mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferenceManager().saveUserProfileDate(userData);

    }

    private void loadPhotoFromGallery() {

        Intent takeGalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                //// TODO: 30.06.2016 обработать ошибку
            }

            if (mPhotoFile != null) {
                //// TODO: 30.06.2016 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);

            }else{
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            }

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSetting();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                //// TODO: 01.07.2016 тут обрабатываем разрешения (разрешение получено)
                // например вывести сообщение или обработать какой-то логикой если нужно
            }
        }
        if (grantResults[1]==PackageManager.PERMISSION_GRANTED){
            //// TODO: 01.07.2016 тут обрабатываем разрешения (разрешение получено)
            // например вывести сообщение или обработать какой-то логикой если нужно
        }
    }

    private void hideProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.GONE);

    }

    private void showProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.VISIBLE);

    }

    private void lockToolbar(){
        mAppBarLayout.setExpanded(true,true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);

    }

    private void unlockToolbar(){
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL| AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery)
                        ,getString(R.string.user_profile_dialog_camera)
                        ,getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                //// TODO: 30.06.2016 загрузить с галлереи
                                loadPhotoFromGallery();
                                //showSnackbar("Загрузить из галереи");
                                break;
                            case 1:
                                //// TODO: 30.06.2016 загрузить с камеры
                                loadPhotoFromCamera();
                                //showSnackbar("Загрузить из камеры");
                                break;
                            case 2:
                                //// TODO: 30.06.2016 отменить
                                dialog.cancel();
                                showSnackbar("Отмена");
                                break;
                        }

                    }
                });
                return builder.create();
            default:
                return null;
        }
    }
    private File createImageFile() throws IOException{
        String timeSetup = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeSetup+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver(). insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        //// TODO: 01.07.2016 сделать плейхолдер и трансформ + crop
        mDataManager.getPreferenceManager().SaveUserPhoto(selectedImage);
    }

    public void openApplicationSetting(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
}




