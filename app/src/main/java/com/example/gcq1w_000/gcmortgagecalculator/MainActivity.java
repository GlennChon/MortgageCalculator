package com.example.gcq1w_000.gcmortgagecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // constants used when saving/restoring state
    private static final String PURCHASE_PRICE = "PURCHASE_PRICE";
    private static final String DOWN_PAYMENT = "DOWN_PAYMENT";
    private static final String INTEREST_RATE = "INTEREST_RATE";
    private static final String CUSTOM_YEAR = "CUSTOM_YEAR";

    private double housePrice; // house price entered by user
    private double downPayment; // downPayment entered by user
    private double interestRate; // interest rate entered by user
    private int currentCustomYear; // custom year set with the yearSeekBar
    private EditText priceEditText; // house price entered by user
    private EditText paymentEditText; // downPayment entered by user
    private EditText interestEditText; // interest rate entered by user
    private EditText loanEditText; // displays loan amount
    private EditText tenYearEditText; // displays monthly payment for 10 year loan
    private EditText twentyYearEditText; // displays monthly payment for 20 year loan
    private EditText thirtyYearEditText; // displays monthly payment for 30 year loan
    private TextView cYearTextView; // Displays custom year selection
    private EditText cLoanEditText; // Displays custom monthly payment

    // Called when activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) { // app just started
            housePrice = 0.0; // initialize house price to zero
            downPayment = 0.0; // initialize down payment to zero
            interestRate = 1.0; // initialize interest rate to one
            currentCustomYear = 15; // initialize custom year to 15
        } else { // app being restored from memory

            // initialize to saved amounts
            housePrice = savedInstanceState.getDouble(PURCHASE_PRICE);
            downPayment = savedInstanceState.getDouble(DOWN_PAYMENT);
            interestRate = savedInstanceState.getDouble(INTEREST_RATE);
            currentCustomYear = savedInstanceState.getInt(CUSTOM_YEAR);
        } // end else

        // get ref to loan
        loanEditText = (EditText) findViewById(R.id.loanEditText);

        // get ref to year 10, 20, 30.
        tenYearEditText = (EditText) findViewById(R.id.tenYearEditText);
        twentyYearEditText = (EditText) findViewById(R.id.twentyYearEditText);
        thirtyYearEditText = (EditText) findViewById(R.id.thirtyYearEditText);

        // get ref to custom year display
        cYearTextView = (TextView) findViewById(R.id.cYearTextView);

        // get ref to custom monthly payment
        cLoanEditText =(EditText) findViewById(R.id.cLoanEditText);

        // get ref to purchase price
        priceEditText = (EditText) findViewById(R.id.priceEditText);
        // priceEditTextWatcher handles priceEditText's onTextChanged event
        priceEditText.addTextChangedListener(priceEditTextWatcher);

        // get ref to payment
        paymentEditText = (EditText) findViewById(R.id.paymentEditText);
        // paymentEditTextWatcher handles priceEditText's onTextChanged event
        paymentEditText.addTextChangedListener(paymentEditTextWatcher);

        // get ref to interest rate
        interestEditText = (EditText) findViewById(R.id.interestEditText);
        // priceEditTextWatcher handles priceEditText's onTextChanged event
        interestEditText.addTextChangedListener(interestEditTextWatcher);

        // get the SeekBar used to set the custom year
        SeekBar yearSeekBar = (SeekBar) findViewById(R.id.yearSeekBar);
        yearSeekBar.setOnSeekBarChangeListener(yearSeekBarListener);
    } // end method onCreate

    // monthly mortgage payment  formula
    private static double monthlyPayment(double interest, double loan, double years) {

        // checks if custom year is 0 and returns full loan amount
        if (years == 0) {

            return loan;
        }else if(loan < 0){

            // return 0 if loan is negative
            return 0;

        }else {

            //else run formula
            // checks if interest is 0 to avoid NaN statement in display fields
            if (interest <= 0){
                interest = 1.0;
            }
            // sets interest rate
            double r = (interest * .01) / 12; // interest% multiplied by .01 divided 12 months

            // sets number of months to spread payment over
            double n = 12 * years; // 23 months

            // finds monthly payment amount
            double m = loan * ((r * (Math.pow((1 + r), n))) / (Math.pow((1 + r), n) - 1));

            // return monthly payment amount
            return m;
        }

    }// end method monthlyPayment

    // Update years 10, 20, 30 EditTexts
    private void updateStandard() {

        // calculate loan amount
        double loanAmount = housePrice - downPayment;

        // if loan amount is negative
        if (loanAmount < 0){
            loanAmount = 0;
        }
        //set loanEditText to loan amount
        loanEditText.setText(String.format("%.02f", loanAmount));

        // set tenYearEditText's text
        tenYearEditText.setText(String.format("%.02f",
                monthlyPayment(interestRate, loanAmount, 10)));

        // set twentyYearEditText's text
        twentyYearEditText.setText(String.format("%.02f",
                monthlyPayment(interestRate, loanAmount, 20)));

        // set thirtyYearEditText's text
        thirtyYearEditText.setText(String.format("%.02f",
                monthlyPayment(interestRate, loanAmount, 30)));
    } // end method updateStandard

    private void updateCustom() {

        // calculate loan amount
        double loanAmount = housePrice - downPayment;

        // if loan amount is negative
        if (loanAmount < 0){
            loanAmount = 0;
        }

        // set cYearTextView's text to match the position of the SeekBar
        cYearTextView.setText(currentCustomYear + " year(s)");

        // set cLoanEditText's text
        cLoanEditText.setText(String.format("%.02f",
                monthlyPayment(interestRate, loanAmount, currentCustomYear)));
    } // end method updateCustom



    // called when the user changes the position of the yearSeekBar
    private SeekBar.OnSeekBarChangeListener yearSeekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                // update currentCustomPercent, then call updateCustom
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                    // sets currentCustomPercent to position of the SeekBar's thumb
                    currentCustomYear = seekBar.getProgress();
                    updateCustom(); // update EditTexts for custom tip and total
                } // end method onProgressChanged

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                } // end method onStartTrackingTouch

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                } // end method onStopTrackingTouch
            }; // end OnSeekBarChangeListener

    // event-handling object that responds to priceEditText's events
    private TextWatcher priceEditTextWatcher = new TextWatcher() {
        // called when the user enters a number
        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            // convert priceEditText's text to a double
            try {
                housePrice = Double.parseDouble(s.toString());
            } // end try
            catch (NumberFormatException e) {
                housePrice = 0.0; // default if an exception occurs
            } // end catch

            // update the standard and custom tip EditTexts
            updateStandard(); // update the 10, 20 and 30 year EditTexts
            updateCustom(); // update the custom EditTexts
        } // end method onTextChanged

        @Override
        public void afterTextChanged(Editable s) {
        } // end method afterTextChanged

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        } // end method beforeTextChanged
    }; // end priceEditTextWatcher

    // event-handling object that responds to paymentEditText's events
    private TextWatcher paymentEditTextWatcher = new TextWatcher() {
        // called when the user enters a number
        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            // convert paymentEditText's text to a double
            try {
                downPayment = Double.parseDouble(s.toString());
            } // end try
            catch (NumberFormatException e) {
                downPayment = 0.0; // default if an exception occurs
            } // end catch

            // update the standard and custom tip EditTexts
            updateStandard(); // update the 10, 20 and 30 year EditTexts
            updateCustom(); // update the custom EditTexts
        } // end method onTextChanged

        @Override
        public void afterTextChanged(Editable s) {
        } // end method afterTextChanged

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        } // end method beforeTextChanged
    }; // end paymentEditTextWatcher

    // event-handling object that responds to paymentEditText's events
    private TextWatcher interestEditTextWatcher = new TextWatcher() {
        // called when the user enters a number
        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            // convert paymentEditText's text to a double
            try {
                interestRate = Double.parseDouble(s.toString());
            } // end try
            catch (NumberFormatException e) {
                interestRate = 1.0; // default if an exception occurs
            } // end catch

            // update the standard and custom tip EditTexts
            updateStandard(); // update the 10, 20 and 30 year EditTexts
            updateCustom(); // update the custom EditText
        } // end method onTextChanged

        @Override
        public void afterTextChanged(Editable s) {
        } // end method afterTextChanged

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        } // end method beforeTextChanged
    }; // end interestEditTextWatcher

    // save values of  housePrice, downPayment, interestRate, currentCustomYear
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(PURCHASE_PRICE, housePrice);
        outState.putDouble(DOWN_PAYMENT, downPayment);
        outState.putDouble(INTEREST_RATE, interestRate);
        outState.putInt(CUSTOM_YEAR, currentCustomYear);
    } // end method onSaveInstanceState

}
