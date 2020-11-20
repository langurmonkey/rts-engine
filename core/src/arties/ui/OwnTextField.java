/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.ui;

import arties.util.parse.Parser;
import arties.util.validator.CallbackValidator;
import arties.util.validator.FloatValidator;
import arties.util.validator.IValidator;
import arties.util.validator.IntValidator;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent;

/**
 * TextButton in which the cursor changes when the mouse rolls over.
 * It also fixes the size issue.
 *
 * @author Toni Sagrista
 */
public class OwnTextField extends TextField {

    private float ownWidth = 0f, ownHeight = 0f;
    private IValidator validator = null;
    private String lastCorrectText = "";
    private Color regularColor;
    private Color errorColor;

    public OwnTextField(String text, Skin skin) {
        super(text, skin);
    }

    public OwnTextField(String text, Skin skin, IValidator validator) {
        this(text, skin);
        this.validator = validator;
        initValidator();
    }

    public OwnTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public OwnTextField(String text, Skin skin, String styleName, IValidator validator) {
        this(text, skin, styleName);
        this.validator = validator;
        initValidator();
    }

    public OwnTextField(String text, TextFieldStyle style) {
        super(text, style);
    }

    public OwnTextField(String text, TextFieldStyle style, IValidator validator) {
        this(text, style);
        this.validator = validator;
        initValidator();
    }

    public void setValidator(IValidator validator) {
        this.validator = validator;
        initValidator();
    }

    public void setErrorColor(Color errorColor) {
        this.errorColor = errorColor;
    }

    /**
     * Checks the validity of the value. If the text field has no validator, all
     * values are valid. If it has a validator, it checks whether the value
     * is ok
     *
     * @return True if the value is valid or the text field has no validator, false otherwise
     */
    public boolean isValid() {
        return this.validator == null || this.validator.validate(this.getText());
    }

    public float getFloatValue(float defaultValue){
        return (float) getDoubleValue(defaultValue);
    }
    public double getDoubleValue(double defaultValue){
        try{
            return Parser.parseFloatException(getText());
        } catch(Exception e){
            return defaultValue;
        }
    }
    public long getIntValue(int defaultValue){
        return (int) getLongValue(defaultValue);
    }
    public long getLongValue(long defaultValue){
        try{
            return Parser.parseLongException(getText());
        }catch(Exception e){
            return defaultValue;
        }
    }

    private void initValidator() {
        if (validator != null) {
            errorColor = new Color(0xff6666ff);
            regularColor = getColor().cpy();
            addListener(event -> {
                if (event instanceof ChangeEvent) {
                    String str = getText();
                    if (validator.validate(str)) {
                        setColor(regularColor);
                        lastCorrectText = str;
                    } else {
                        setColor(errorColor);
                    }
                    return true;
                } else if (event instanceof FocusEvent) {
                    if (!((FocusEvent) event).isFocused()) {
                        // We lost focus, return to last correct text if current not valid
                        String str = getText();
                        if (!validator.validate(str)) {
                            setText(lastCorrectText);
                            setColor(regularColor);
                        }

                    }
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public void setWidth(float width) {
        ownWidth = width;
        super.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        ownHeight = height;
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        ownWidth = width;
        ownHeight = height;
        super.setSize(width, height);
    }

    @Override
    public float getPrefWidth() { //-V6052
        if (ownWidth != 0) {
            return ownWidth;
        } else {
            return super.getPrefWidth();
        }
    }

    @Override
    public float getPrefHeight() { //-V6052
        if (ownHeight != 0) {
            return ownHeight;
        } else {
            return super.getPrefHeight();
        }
    }

}