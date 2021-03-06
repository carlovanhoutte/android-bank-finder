package com.webcomrades.bankfinder;

import java.io.IOException;
import java.util.List;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.preference.PreferenceManager;

import com.google.common.collect.Lists;
import com.webcomrades.bankfinder.controller.BrandManager;
import com.webcomrades.bankfinder.controller.DataController;
import com.webcomrades.bankfinder.controller.ErrorHandler;
import com.webcomrades.bankfinder.controller.ImageViewController;
import com.webcomrades.bankfinder.datafetcher.HttpURLDataFetcher;
import com.webcomrades.bankfinder.function.GetBrandsFromApplicationSettings;
import com.webcomrades.bankfinder.function.GetBrandsUpdater;
import com.webcomrades.bankfinder.model.Brand;

/**
 * @author Jo Somers - sayhello@josomers.be
 * @since 2013
 */

@ReportsCrashes(formKey = BankFinderGlobals.ACRA_KEY,
customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, 
		ReportField.ANDROID_VERSION, ReportField.BRAND, ReportField.PHONE_MODEL, ReportField.PRODUCT, 
		ReportField.STACK_TRACE, ReportField.DISPLAY, ReportField.INITIAL_CONFIGURATION, ReportField.USER_CRASH_DATE },
mode=ReportingInteractionMode.SILENT)
public class BankFinder extends Application {
	
	private static ImageViewController imageViewController;
	private static BrandManager brandsManager;
	private static DataController networkController;
	private static ErrorHandler errorHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ACRA.init(this);
		
		imageViewController = new ImageViewController(getApplicationContext(), BankFinderGlobals.HTTP + BankFinderGlobals.getBaseUrl());
		brandsManager = new BrandManager(getBrandsFromSettings());
		networkController = new DataController(BankFinderGlobals.PATH_BANK, BankFinderGlobals.PATH_BRAND, new HttpURLDataFetcher(getBaseUrl(), 10000, 30000));	
		errorHandler = new ErrorHandler(BankFinderGlobals.inProductionMode());
		
		saveVersionCode();
		getBrandsFromServer();
	}
	
	private static String getBaseUrl() {
		return BankFinderGlobals.HTTP + BankFinderGlobals.getBaseUrl() + BankFinderGlobals.API;
	}
	
	private List<Brand> getBrandsFromSettings() {
		List<Brand> brands = Lists.newArrayList();
		
		try {
			brands = GetBrandsFromApplicationSettings.F.apply(getApplicationContext());
		} catch (NotFoundException e) {
			errorHandler.handleError(e);
		} catch (IOException e) {
			errorHandler.handleError(e);
		}
		
		return brands;
	}

	private void getBrandsFromServer() {
		GetBrandsUpdater.F.apply(getApplicationContext());
	}

	private void saveVersionCode() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(BankFinderGlobals.PREFERENCE_VERSION_CODE, packageInfo.versionCode);
		} catch (NameNotFoundException e) {
			errorHandler.handleError(e);
		}
	}
	
	public static ImageViewController getImageViewController() {
		return imageViewController;
	}
	
	public static DataController getNetworkController() {
		return networkController;
	}
	
	public static BrandManager getBrandsManager() {
		return brandsManager;
	}
	
	public static ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
}
