package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

/**
 * Created by me, myself and i
 */
public class LEDBrick extends BrickBaseType implements OnClickListener, FormulaBrick {

    private transient TextView editLightValue;
    private Formula lightValue;

    private transient View prototypeView;

    public LEDBrick( Sprite sprite, Formula lightValue ) {
        this.sprite = sprite;
        this.lightValue = lightValue;
    }

    public LEDBrick( Sprite sprite ) {
        this.sprite = sprite;
        this.lightValue = new Formula( 0 );
    }

    @Override
    public Brick copyBrickForSprite( Sprite sprite, Script script ) {
        LEDBrick copyBrick = (LEDBrick) clone();
        copyBrick.sprite = sprite;
        return copyBrick;
    }

    @Override
    public View getView( Context context, int brickId, BaseAdapter baseAdapter ) {
        if ( animationState ) {
            return view;
        }

        if ( view == null ) {
            alphaValue = 0xFF;
        }

        view = View.inflate( context, R.layout.brick_led, null );
        view = getViewWithAlpha( alphaValue );

        setCheckboxView( R.id.brick_led_checkbox );

        final Brick brickInstance = this;
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                adapter.handleCheck(brickInstance, isChecked);
            }
        });

        TextView textLED = (TextView) view.findViewById( R.id.brick_led_prototype_text_view );
        TextView editLED = (TextView) view.findViewById( R.id.brick_led_edit_text );

        lightValue.setTextFieldId( R.id.brick_led_edit_text );
        lightValue.refreshTextField( view );

        textLED.setVisibility( View.GONE );
        editLED.setVisibility( View.VISIBLE );
        editLED.setOnClickListener( this );

        return view;
    }

    @Override
    public View getViewWithAlpha( int alphaValue ) {
        if (view != null) {
            View layout = view.findViewById( R.id.brick_led_layout );
            Drawable background = layout.getBackground();
            background.setAlpha( alphaValue );

            TextView textLED = (TextView) view.findViewById( R.id.brick_led_label );
            TextView editLED = (TextView) view.findViewById( R.id.brick_led_edit_text );
            textLED.setTextColor( textLED.getTextColors().withAlpha( alphaValue ));
            editLED.setTextColor( textLED.getTextColors().withAlpha( alphaValue ));
            editLED.getBackground().setAlpha( alphaValue );

            this.alphaValue = (alphaValue);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if ( checkbox.getVisibility() == View.VISIBLE ) {
            return;
        }
        FormulaEditorFragment.showFragment(view, this, lightValue);
    }

    @Override
    public Formula getFormula() {
        return lightValue;
    }

    @Override
    public List<SequenceAction> addActionToSequence( SequenceAction sequence ) {
        sequence.addAction( ExtendedActions.lights( this.sprite, lightValue ) );
        return null;
    }
    
    @Override
    public View getPrototypeView( Context context ) {
        prototypeView = View.inflate( context, R.layout.brick_led, null );
        TextView ledTextView = (TextView) prototypeView.findViewById( R.id.brick_led_prototype_text_view );
        ledTextView.setText( String.valueOf( lightValue.interpretBoolean( sprite )));
        return prototypeView;
    }

    @Override
    public Brick clone() {
        return new LEDBrick( getSprite(), lightValue.clone() );
    }

    @Override
    public int getRequiredResources() {
        return CAMERA_LED;
    }
}
