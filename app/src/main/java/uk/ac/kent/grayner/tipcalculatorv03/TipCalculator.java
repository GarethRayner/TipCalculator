package uk.ac.kent.grayner.tipcalculatorv03;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TipCalculator extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        //Initialise and load preference data.
        SharedPreferences percentData = this.getPreferences(Context.MODE_PRIVATE);

        //Retrieve the appropriate EditText for the tip percentage.
        final EditText tipP = (EditText) findViewById(R.id.tipPercent);
        final EditText shared = (EditText) findViewById(R.id.sharedBy);
        final EditText bill = (EditText) findViewById(R.id.billAmount);

        //Fetch the preference data if it exists...
        if(percentData.getFloat("tip", 0.0f) == 0.0f) {
            //If not, set the tip percentage to 10.0.
            tipP.setText(Float.toString(10.0f));
        } else {
            //If it is, set it to the value retrieved.
            tipP.setText(Float.toString(percentData.getFloat("tip", 0.0f)));
        }

        /*  Added a textChange listener to all three fields. tipP event listener records the tip to
            preferences if it isn't an empty string. */
        tipP.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable e) {
                calculate(tipP);
                if(!(tipP.getText().toString().compareTo("") == 0)) {
                    recordTip(Float.parseFloat(tipP.getText().toString()));
                }
            }
            public void beforeTextChanged(CharSequence c, int s, int co, int a) {

            }
            public void onTextChanged(CharSequence c, int s, int co, int a) {

            }
        });

        bill.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable e) {
                calculate(bill);
            }
            public void beforeTextChanged(CharSequence c, int s, int co, int a) {

            }
            public void onTextChanged(CharSequence c, int s, int co, int a) {

            }
        });

        shared.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable e) {
                calculate(shared);
            }
            public void beforeTextChanged(CharSequence c, int s, int co, int a) {

            }
            public void onTextChanged(CharSequence c, int s, int co, int a) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tip_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The main method of this app, it takes all values specified, calculates the appropriate amount
     * to tip, the bill with the tip added and the rounded share for the bill up to the appropriate
     * currency note.
     *
     * @param view
     */
    public void calculate(View view) {
        //Retrieve and store references to all appropriate fields.
        EditText billAmount = (EditText) findViewById(R.id.billAmount);
        EditText tipPercent = (EditText) findViewById(R.id.tipPercent);
        EditText sharedBy = (EditText) findViewById(R.id.sharedBy);

        //Retrieve and store references to all appropriate displays.
        TextView recTip = (TextView) findViewById(R.id.recTip);
        TextView billTotal = (TextView) findViewById(R.id.billTotal);
        TextView shareTotal = (TextView) findViewById(R.id.shareTotal);

        //Retrieve values from every field.
        String billS = billAmount.getText().toString();
        String tipS = tipPercent.getText().toString();
        String sharedS = sharedBy.getText().toString();

        //Check for any unexpected conditions and show an error if so.
        if(billS.compareTo("") == 0 || tipS.compareTo("") == 0 || sharedS.compareTo("") == 0) {
            showToast("invalidFields");
            return;
        } else if(Float.parseFloat(billS) == 0.0f) {
            showToast("zeroBill");
            return;
        }

        //Parse all strings retrieved earlier into float values for calculations.
        float bill = Float.parseFloat(billS);
        float tip = Float.parseFloat(tipS);
        int shared = Integer.parseInt(sharedS);

        //Record the tip percentage used.
        recordTip(tip);

        //Check to see if there are any unexpected conditions at this point. Show error if so.
        if(shared == 0) {
            showToast("zeroShare");
            return;
        } else if(tip == 0 || tip > 50) {
            showToast("incorrectTip");
            return;
        }

        //Perform calculations. First uses mathematical formula to find the percentage specified.
        float tipSize = (bill * (tip/100));
        float tipAdded = (bill + tipSize);
        float share = (tipAdded / shared);

        /*  Begin rounding. First if the value is under or equal to 10.00, it checks against every
            coin or note in circulation excluding copper coins or £50 note to see if the value is
            under or equal to that coin/note. If so, it will round to that coin/note. If not, the
            program will move to check the next value. */
        if(share <= 0.10f) {
            /*  Each calculation is exactly the same - round up by subtracting the current share
                value from the desired value and adding the remainder onto the share value. */
            share = share + (0.10f - share);
        } else if(share <= 0.20f) {
            share = share + (0.20f - share);
        } else if(share <= 0.50f) {
            share = share + (0.50f - share);
        } else if(share <= 1.00f) {
            share = share + (1.00f - share);
        } else if(share <= 2.00f) {
            share = share + (2.00f - share);
        } else if(share <= 5.00f) {
            share = share + (5.00f - share);
        } else if(share <= 10.00f) {
            share = share + (10.00f - share);
        } else if((share % 10) != 0) {
            /*  If the share value is above 10.00f, then perform modulo to obtain remainder and
                perform checks against each coin/note once again. This time it subtracts the
                remainder from modulus from the coin/note value to obtain the needed value to round
                up to that currency value.
             */
            float value = share % 10;
            if(value <= 0.10f) {
                share = share + (0.10f - value);
            } else if(value <= 0.20f) {
                share = share + (0.20f - value);
            } else if(value <= 0.50f) {
                share = share + (0.50f - value);
            } else if(value <= 1.00f) {
                share = share + (1.00f - value);
            } else if(value <= 2.00f) {
                share = share + (2.00f - value);
            } else if(value <= 5.00f) {
                share = share + (5.00f - value);
            } else if(value <= 10.00f) {
                share = share + (10.00f - value);
            }
        }

        //Convert all calculated results into String format at 2 decimal places.
        String tipRounded = String.format("%.02f", tipSize);
        String billTotRounded = String.format("%.02f", tipAdded);
        String shareRounded = String.format("%.02f", share);

        //Set all displays with their appropriate value.
        recTip.setText(tipRounded);
        billTotal.setText(billTotRounded);
        shareTotal.setText(shareRounded);
    }

    /**
     * The error message method for displaying error messages. It checks the String of arg and
     * displays the appropriate error message or "Toast".
     *
     * @param arg
     */
    public void showToast(String arg) {
        //Retrieve application context and initialise String for message.
        Context context = getApplicationContext();
        CharSequence message = "";

        //Check to see which error has been triggered and set message appropriately.
        if(arg.compareTo("invalidFields") == 0) {
            message = "Please fill in all fields!";
        } else if(arg.compareTo("zeroShare") == 0) {
            message = "Cannot share between zero people.";
        } else if(arg.compareTo("incorrectTip") == 0) {
            message = "Tip must be between 1% or 50%.";
        } else if(arg.compareTo("zeroBill") == 0) {
            message = "Bill must be more than zero.";
        }

        //Set the duration, create a Toast object and then display it.
        int duration = Toast.LENGTH_SHORT;
        Toast error = Toast.makeText(context, message, duration);
        error.show();
    }

    /**
     * This method simply saves the tip percentage given to it into the preferences file for later
     * use upon app initialisation.
     *
     * @param tipP
     */
    public void recordTip(float tipP) {
        //Initialise preference file and editor.
        SharedPreferences percentData = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor modifier = percentData.edit();

        //Save the new tip percentage value and commit changes.
        modifier.putFloat("tip", tipP);
        modifier.commit();
    }
}