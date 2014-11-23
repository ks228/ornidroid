package fr.ornidroid.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ornidroid.R;
import fr.ornidroid.bo.MultiCriteriaSearchFormBean;
import fr.ornidroid.helper.Constants;
import fr.ornidroid.service.IOrnidroidService;
import fr.ornidroid.service.OrnidroidServiceFactory;
import fr.ornidroid.ui.components.progressbar.ProgressActionHandler;
import fr.ornidroid.ui.components.progressbar.ProgressActionHandler.ProgressActionCallback;
import fr.ornidroid.ui.components.progressbar.ProgressActionHandlerThread;
import fr.ornidroid.ui.components.progressbar.ProgressActionLoaderInfo;
import fr.ornidroid.ui.multicriteriasearch.MultiCriteriaSearchFieldType;
import fr.ornidroid.ui.multicriteriasearch.MultiCriteriaSelectField;
import fr.ornidroid.ui.multicriteriasearch.MyCustomAdapter;
import fr.ornidroid.ui.multicriteriasearch.OnSpinnersItemSelected;

/**
 * The Class MultiCriteriaSearchActivity.
 */
@EActivity(R.layout.multicriteriasearch)
public class MultiCriteriaSearchActivity extends AbstractOrnidroidActivity
		implements ProgressActionCallback {
	/** The field list. */
	private final List<MultiCriteriaSelectField> fieldList = new ArrayList<MultiCriteriaSelectField>();

	/** The form bean. */
	private final MultiCriteriaSearchFormBean formBean = new MultiCriteriaSearchFormBean();

	/** The m loader. */
	private ProgressActionHandlerThread mLoader;

	/** The nb results text view. */
	@ViewById(R.id.search_nb_results)
	TextView nbResultsTextView;

	/** The ornidroid service. */
	private final IOrnidroidService ornidroidService = OrnidroidServiceFactory
			.getService(this);

	/** The progress bar. */
	private ProgressDialog progressBar;

	/** The reset form button. */
	@ViewById(R.id.reset_form)
	ImageView resetFormButton;

	/** The show results clickable area. */
	@ViewById(R.id.search_show_results)
	LinearLayout showResultsClickableArea;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ornidroid.ui.multicriteriasearch.ProgressActionHandler.
	 * ProgressActionCallback#doAction()
	 */
	public void doAction() {
		this.ornidroidService
				.getBirdMatchesFromMultiSearchCriteria(this.formBean);
	}

	/**
	 * Gets the form bean.
	 * 
	 * @return the form bean
	 */
	public MultiCriteriaSearchFormBean getFormBean() {
		return this.formBean;
	}

	/**
	 * Gets the ornidroid service.
	 * 
	 * @return the ornidroid service
	 */
	public IOrnidroidService getOrnidroidService() {
		return this.ornidroidService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ornidroid.ui.components.progressbar.ProgressActionHandler.
	 * ProgressActionCallback
	 * #onActionEnded(fr.ornidroid.ui.components.progressbar
	 * .ProgressActionHandler,
	 * fr.ornidroid.ui.components.progressbar.LoaderInfoMCS)
	 */
	public void onActionEnded(final ProgressActionHandler loader,
			final ProgressActionLoaderInfo info) {
		killLoader();
		final Intent intent = new Intent(getApplicationContext(),
				MainActivity_.class);
		intent.putExtra(MainActivity_.SHOW_SEARCH_FIELD_INTENT_PRM, false);
		startActivity(intent);

	}

	/**
	 * Onshow results clickable area click.
	 */
	@Click(R.id.search_show_results)
	public void onshowResultsClickableAreaClick() {

		if (this.mLoader == null) {
			this.mLoader = new ProgressActionHandlerThread();
			this.mLoader.start();
		}

		this.mLoader.startAction(this);
		this.progressBar = new ProgressDialog(this);
		this.progressBar.setCancelable(false);
		this.progressBar.setMessage(this.getResources().getText(
				R.string.search_please_wait));
		this.progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		this.progressBar.show();

	}

	/**
	 * After views.
	 */
	@AfterViews
	void afterViews() {

		initSelectField(MultiCriteriaSearchFieldType.CATEGORY);
		initSelectField(MultiCriteriaSearchFieldType.COUNTRY);
		initSelectField(MultiCriteriaSearchFieldType.SIZE);
		initSelectField(MultiCriteriaSearchFieldType.FEATHER_COLOUR);
		initSelectField(MultiCriteriaSearchFieldType.BEAK_COLOUR);
		initSelectField(MultiCriteriaSearchFieldType.FEET_COLOUR);
		initSelectField(MultiCriteriaSearchFieldType.HABITAT);
		initSelectField(MultiCriteriaSearchFieldType.BEAK_FORM);
		initSelectField(MultiCriteriaSearchFieldType.REMARKABLE_SIGN);
		updateSearchCountResults(this.ornidroidService
				.getMultiSearchCriteriaCountResults(this.formBean));
	}

	/**
	 * Returns the type of the field to which the adapter view belongs.
	 * 
	 * @param parent
	 *            the parent
	 * @return the select type
	 */
	public MultiCriteriaSearchFieldType getSelectType(
			final AdapterView<?> parent) {
		if (MultiCriteriaSelectField.class.isInstance(parent.getParent())) {
			final MultiCriteriaSelectField field = (MultiCriteriaSelectField) parent
					.getParent();
			return field.getFieldType();

		}
		return null;
	}

	/**
	 * Update the search count results button.
	 * 
	 * @param countResults
	 *            the count results
	 */
	public void updateSearchCountResults(final int countResults) {
		this.nbResultsTextView.setText(countResults + Constants.BLANK_STRING
				+ this.getText(R.string.search_results));
	}

	/**
	 * Inits the select field.
	 * 
	 * @param selectFieldType
	 *            the select field type
	 */
	private void initSelectField(
			final MultiCriteriaSearchFieldType selectFieldType) {
		ArrayAdapter<String> dataAdapter = null;
		MultiCriteriaSelectField field = null;
		switch (selectFieldType) {
		case CATEGORY:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_category_field);
			field.setIconResource(R.drawable.ic_categories);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getCategories());

			break;
		case COUNTRY:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_country_field);
			field.setIconResource(R.drawable.ic_countries);
			// dataAdapter = new ArrayAdapter<String>(this,
			// android.R.layout.simple_spinner_item,
			// this.ornidroidService.getCountries());
			dataAdapter = new MyCustomAdapter(this,
					R.layout.row_spinner_icons_dropdown_list,
					this.ornidroidService.getCountries(), selectFieldType);
			break;
		case SIZE:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_size_field);
			field.setIconResource(R.drawable.ic_size);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getSizes());
			break;

		case HABITAT:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_habitat_field);
			field.setIconResource(R.drawable.ic_habitat);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getHabitats());
			break;
		case BEAK_FORM:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_beak_form_field);
			field.setIconResource(R.drawable.ic_beak_type);
			dataAdapter = new MyCustomAdapter(this, R.layout.row_spinner_icons,
					this.ornidroidService.getBeakForms(), selectFieldType);
			break;
		case FEATHER_COLOUR:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_feather_colour_field);
			field.setIconResource(R.drawable.ic_feather_colour);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getColours());
			break;
		case BEAK_COLOUR:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_beak_colour_field);
			field.setIconResource(R.drawable.ic_beak_colour);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getColours());
			break;
		case FEET_COLOUR:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_paw_colour_field);
			field.setIconResource(R.drawable.ic_feet_colour);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getColours());
			break;
		case REMARKABLE_SIGN:
			field = (MultiCriteriaSelectField) findViewById(R.id.search_remarkable_sign_field);
			field.setIconResource(R.drawable.ic_remarkable_sign);
			dataAdapter = new ArrayAdapter<String>(this,
					R.layout.row_spinner_without_icons,
					this.ornidroidService.getRemarkableSigns());
			break;

		}
		field.setFieldType(selectFieldType);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		field.getSpinner().setAdapter(dataAdapter);
		field.getSpinner().setOnItemSelectedListener(
				new OnSpinnersItemSelected(this));
		this.fieldList.add(field);

	}

	/**
	 * Kill loader.
	 */
	private void killLoader() {
		this.mLoader.quit();
		this.mLoader = null;
		this.progressBar.dismiss();
	}

	/**
	 * Reset all the select fields in the form.
	 */
	@Click(R.id.reset_form)
	void resetForm() {
		for (final MultiCriteriaSelectField field : this.fieldList) {
			field.reset();
		}
	}
}
