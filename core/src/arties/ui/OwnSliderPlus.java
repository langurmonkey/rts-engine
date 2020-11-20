/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.ui;

import arties.util.MathUtilsd;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

import java.text.DecimalFormat;

/**
 * Same as a regular slider, but contains the title (name) and the value within its bounds.
 */
public class OwnSliderPlus extends Slider {

    private OwnSliderPlus me;
    private float ownwidth = 0f, ownheight = 0f;
    private float mapMin, mapMax;
    private boolean map = false;
    private Skin skin;
    private OwnLabel titleLabel, valueLabel;
    private boolean displayValueMapped = false;
    private String valuePrefix, valueSuffix;
    private float padX = 3f;
    private float padY = 2f;
    private DecimalFormat nf;

    public OwnSliderPlus(String title, float min, float max, float stepSize, float mapMin, float mapMax, Skin skin) {
        super(min, max, stepSize, false, skin, "big-horizontal");
        this.skin = skin;
        setUp(title, mapMin, mapMax, "default");
    }

    public OwnSliderPlus(String title, float min, float max, float stepSize, Skin skin) {
        super(min, max, stepSize, false, skin, "big-horizontal");
        this.skin = skin;
        setUp(title, min, max, "default");
    }

    public OwnSliderPlus(String title, float min, float max, float stepSize, boolean vertical, Skin skin) {
        super(min, max, stepSize, vertical, skin, "big-horizontal");
        this.skin = skin;
        setUp(title, min, max, "default");
    }

    public OwnSliderPlus(String title, float min, float max, float stepSize, boolean vertical, Skin skin, String labelStyleName) {
        super(min, max, stepSize, vertical, skin, "big-horizontal");
        this.skin = skin;
        setUp(title, min, max, labelStyleName);
    }

    public void setUp(String title, float mapMin, float mapMax, String labelStyleName) {
        setUp(title, mapMin, mapMax, new DecimalFormat("####0.##"), labelStyleName);
    }

    public void setUp(String title, float mapMin, float mapMax, DecimalFormat nf, String labelStyleName) {
        this.me = this;
        this.nf = nf;
        setMapValues(mapMin, mapMax);

        if (title != null && !title.isEmpty()) {
            this.titleLabel = new OwnLabel(title, skin, labelStyleName);
        } else {
            this.titleLabel = null;
        }

        this.valueLabel = new OwnLabel(getValueString(), skin, labelStyleName);
        this.addListener((event) -> {
            if (event instanceof ChangeEvent) {
                this.valueLabel.setText(getValueString());
                return true;
            }
            return false;
        });
        this.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (actor == me) {
                    if (focused)
                        me.setLabelColor(1, 1, 0, 1);
                    else
                        me.restoreLabelColor();
                }
            }
        });
    }

    public void setNumberFormatter(DecimalFormat nf) {
        this.nf = nf;
    }

    public void setDisplayValueMapped(boolean displayValueMapped) {
        this.displayValueMapped = displayValueMapped;
    }

    public void setMapValues(float mapMin, float mapMax) {
        this.mapMin = mapMin;
        this.mapMax = mapMax;
        this.map = mapMin != getMinValue() || mapMax != getMaxValue();
    }

    public void removeMapValues() {
        this.mapMin = 0;
        this.mapMax = 0;
        this.map = false;
    }

    public String getValueString() {
        return (valuePrefix != null ? valuePrefix : "") + nf.format((displayValueMapped ? getMappedValue() : getValue())) + (valueSuffix != null ? valueSuffix : "");
    }

    public float getMappedValue() {
        if (map) {
            return MathUtilsd.lint(getValue(), getMinValue(), getMaxValue(), mapMin, mapMax);
        } else {
            return getValue();
        }
    }

    public void setMappedValue(double mappedValue) {
        setMappedValue((float) mappedValue);
    }

    public void setMappedValue(float mappedValue) {
        if (map) {
            setValue(MathUtilsd.lint(mappedValue, mapMin, mapMax, getMinValue(), getMaxValue()));
        } else {
            setValue(mappedValue);
        }
    }

    public void setValuePrefix(String valuePrefix) {
        this.valuePrefix = valuePrefix;
    }

    public void setValueSuffix(String valueSuffix) {
        this.valueSuffix = valueSuffix;
    }

    @Override
    public void setWidth(float width) {
        ownwidth = width;
        super.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        ownheight = height;
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        ownwidth = width;
        ownheight = height;
        super.setSize(width, height);
    }

    @Override
    public float getPrefWidth() {
        if (ownwidth != 0) {
            return ownwidth;
        } else {
            return super.getPrefWidth();
        }
    }

    @Override
    public float getPrefHeight() {
        if (ownheight != 0) {
            return ownheight;
        } else {
            return super.getPrefHeight();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (titleLabel != null) {
            titleLabel.setPosition(getX() + padX, getY() + getHeight() - titleLabel.getHeight() - padY);
            titleLabel.draw(batch, parentAlpha);
        }
        if (valueLabel != null) {
            valueLabel.setPosition(getX() + getPrefWidth() - (valueLabel.getPrefWidth() + padX * 2f), getY() + getHeight() - valueLabel.getHeight() - padY);
            valueLabel.draw(batch, parentAlpha);
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (valueLabel != null)
            valueLabel.setDisabled(disabled);
        if (titleLabel != null)
            titleLabel.setDisabled(disabled);
    }

    private Color labelColorBackup;

    public void setLabelColor(float r, float g, float b, float a) {
        if (this.titleLabel != null) {
            labelColorBackup = this.titleLabel.getColor().cpy();
            this.titleLabel.setColor(r, g, b, a);
            if (this.valueLabel != null) {
                this.valueLabel.setColor(r, g, b, a);
            }
        }
    }

    public void restoreLabelColor() {
        if (labelColorBackup != null && this.titleLabel != null) {
            this.titleLabel.setColor(labelColorBackup);
            if (this.valueLabel != null) {
                this.valueLabel.setColor(labelColorBackup);
            }
        }
    }

}