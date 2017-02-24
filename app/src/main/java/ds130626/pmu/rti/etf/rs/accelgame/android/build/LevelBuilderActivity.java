package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import ds130626.pmu.rti.etf.rs.accelgame.R;
import ds130626.pmu.rti.etf.rs.accelgame.android.LevelLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.android.SimpleLevelLoader;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;

public class LevelBuilderActivity extends AppCompatActivity implements View.OnTouchListener, BuilderModel.NotificationListener {
    BuilderModel model;
    BuilderImageView view;
    PixelConverter converter;
    SelectionModel selectionModel;
    DrawerLayout drawer;
    LevelLoader loader;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_builder);
        selectionModel = new SelectionModel();
        converter = new PixelConverter(getResources().getDisplayMetrics().density);
        loader = new SimpleLevelLoader(getApplicationContext());
        view = (BuilderImageView) findViewById(R.id.builderImageView);
        view.setPixelConverter(converter);
        view.setOnTouchListener(this);
        drawer = (DrawerLayout) findViewById(R.id.activity_level_builder);
        if (model == null) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            model = new BuilderModel(converter.dpFromPx(size.x), converter.dpFromPx(size.y), view, this);
            view.setModel(model);
        }
        initBallSelection();
        initGoalSelection();
        initObstacleSelection();
        initTrapSelection();
        initBackgroundSelection();
        initButtons();
    }

    private void showSaveDialog(final boolean quitButton, String nameText, final boolean quitOnSave) {
        final EditText editText = new EditText(this);
        editText.setText(nameText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save level");
        builder.setMessage("Enter level name:");
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        if (quitButton) {
            builder.setNeutralButton("Just Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        final AlertDialog dialog = builder.create();

        dialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String levelName = editText.getText().toString();
                if (levelName.isEmpty()) {
                    onMessage("Input the level name");
                    editText.requestFocus();
                    return;
                }
                saveModel(levelName);
                if (quitOnSave) {
                    finish();
                }
                dialog.dismiss();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        if (quitButton) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    dialog.dismiss();
                }
            });
        }
    }

    private void initButtons() {
        Button b = (Button) findViewById(R.id.buttonSaveAs);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Level.Builder builder = model.getBuilder();
                if (!builder.hasBall()) {
                    onMessage("You have to add a ball!");
                    return;
                }
                if (!builder.hasGoal()) {
                    onMessage("You have to add a goal!");
                    return;
                }
                showSaveDialog(false, builder.getName(), false);
            }
        });

        b = (Button) findViewById(R.id.buttonSave);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Level.Builder builder = model.getBuilder();
                if (!builder.hasBall()) {
                    onMessage("You have to add a ball!");
                    return;
                }
                if (!builder.hasGoal()) {
                    onMessage("You have to add a goal!");
                    return;
                }
                if (model.isSaved()) {
                    onMessage("Nothing to save");
                    return;
                }
                if (builder.hasName()) {
                    saveModel(null);
                } else {
                    showSaveDialog(false, builder.getName(), false);
                }
            }
        });
        b = (Button) findViewById(R.id.buttonUndo);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.undo();
            }
        });
        b = (Button) findViewById(R.id.buttonClear);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.reset();
            }
        });
    }

    private void saveModel(String levelName) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache();
        if (levelName == null) {
            model.saveLevel(loader, bitmap, getResources().getDisplayMetrics().density);
        } else {
            model.saveLevel(levelName, loader, bitmap, getResources().getDisplayMetrics().density);
        }
        view.destroyDrawingCache();
    }

    private void initBackgroundSelection() {
        final PreviewImageView imageView = (PreviewImageView) findViewById(R.id.backGroundPreview);
        imageView.setPreview(new DrawablePreview(selectionModel) {
            @Override
            public void draw(Canvas canvas, Paint paint, PixelConverter converter) {
                paint.reset();
                paint.setColor(selectionModel.getBackgroundColor());
                canvas.drawPaint(paint);
            }
        });
        imageView.setPixelConverter(converter);

        SeekBar color = (SeekBar) findViewById(R.id.backGroundColorSeeker);
        initColorPicker(color);
        color.setProgress(selectionModel.getBackgroundColorProgress());
        seekBarDrawerHack(color);
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setBackgroundColorProgress(i);
                int color = getColorFromProgress(i);
                selectionModel.setBackgroundColor(color);
                model.setBackground(color);
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void initTrapSelection() {
        final PreviewImageView imageView = (PreviewImageView) findViewById(R.id.trapPreview);
        imageView.setPreview(new DrawablePreview(selectionModel) {
            @Override
            public void draw(Canvas canvas, Paint paint, PixelConverter converter) {
                paint.reset();
                paint.setColor(selectionModel.getTrapColorSecondary());
                float x = canvas.getWidth() / 2;
                float y = canvas.getHeight() / 2;
                canvas.drawCircle(x, y, converter.pxFromDp(selectionModel.getTrapRadius()) * 1.2f, paint);
                paint.setColor(selectionModel.getTrapColorPrimary());
                canvas.drawCircle(x, y, converter.pxFromDp(selectionModel.getTrapRadius()), paint);
            }
        });
        imageView.setPixelConverter(converter);

        Button btn = (Button) findViewById(R.id.buttonSelectTrap);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionModel.setSelectedObject(SelectionModel.TRAP);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        SeekBar radius = (SeekBar) findViewById(R.id.trapRadiusSeeker);
        radius.setMax(20);
        radius.setProgress(selectionModel.getTrapRadius() - 10);
        seekBarDrawerHack(radius);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    selectionModel.setTrapRadius(seekBar.getProgress() + 10);
                    imageView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar color = (SeekBar) findViewById(R.id.trapPrimarySeeker);
        initColorPicker(color);
        color.setProgress(selectionModel.getTrapColorPrimaryProgress());
        seekBarDrawerHack(color);
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setTrapColorPrimaryProgress(i);
                selectionModel.setTrapColorPrimary(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar colorSecondary = (SeekBar) findViewById(R.id.trapSecondarySeeker);
        initColorPicker(colorSecondary);
        colorSecondary.setProgress(selectionModel.getTrapColorSecondaryProgress());
        seekBarDrawerHack(colorSecondary);
        colorSecondary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setTrapColorSecondaryProgress(i);
                selectionModel.setTrapColorSecondary(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initObstacleSelection() {
        final PreviewImageView imageView = (PreviewImageView) findViewById(R.id.obstaclePreview);
        imageView.setPreview(new DrawablePreview(selectionModel) {
            @Override
            public void draw(Canvas canvas, Paint paint, PixelConverter converter) {
                paint.reset();
                paint.setColor(selectionModel.getObstacleColor());
                canvas.drawPaint(paint);
            }
        });
        imageView.setPixelConverter(converter);

        Button btn = (Button) findViewById(R.id.buttonSelectObstacle);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionModel.setSelectedObject(SelectionModel.OBSTACLE);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        SeekBar color = (SeekBar) findViewById(R.id.obstacleColorSeeker);
        initColorPicker(color);
        color.setProgress(selectionModel.getObstacleColorProgress());
        seekBarDrawerHack(color);
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setObstacleColorProgress(i);
                selectionModel.setObstacleColor(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initGoalSelection() {
        final PreviewImageView imageView = (PreviewImageView) findViewById(R.id.goalPreview);
        imageView.setPreview(new DrawablePreview(selectionModel) {
            @Override
            public void draw(Canvas canvas, Paint paint, PixelConverter converter) {
                paint.reset();
                paint.setColor(selectionModel.getGoalColorSecondary());
                float x = canvas.getWidth() / 2;
                float y = canvas.getHeight() / 2;
                canvas.drawCircle(x, y, converter.pxFromDp(selectionModel.getGoalRadius()) * 1.2f, paint);
                paint.setColor(selectionModel.getGoalColorPrimary());
                canvas.drawCircle(x, y, converter.pxFromDp(selectionModel.getGoalRadius()), paint);
            }
        });
        imageView.setPixelConverter(converter);

        Button btn = (Button) findViewById(R.id.buttonSelectGoal);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionModel.setSelectedObject(SelectionModel.GOAL);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        SeekBar radius = (SeekBar) findViewById(R.id.goalRadiusSeeker);
        radius.setMax(20);
        radius.setProgress((int) selectionModel.getGoalRadius() - 10);
        seekBarDrawerHack(radius);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    selectionModel.setGoalRadius(seekBar.getProgress() + 10);
                    imageView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar color = (SeekBar) findViewById(R.id.goalPrimarySeeker);
        initColorPicker(color);
        color.setProgress(selectionModel.getGoalColorPrimaryProgress());
        seekBarDrawerHack(color);
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setGoalColorPrimaryProgress(i);
                selectionModel.setGoalColorPrimary(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar colorSecondary = (SeekBar) findViewById(R.id.goalSecondarySeeker);
        initColorPicker(colorSecondary);
        colorSecondary.setProgress(selectionModel.getGoalColorSecondaryProgress());
        seekBarDrawerHack(colorSecondary);
        colorSecondary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setGoalColorSecondaryProgress(i);
                selectionModel.setGoalColorSecondary(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initBallSelection() {
        final PreviewImageView imageView = (PreviewImageView) findViewById(R.id.ballPreview);
        imageView.setPreview(new DrawablePreview(selectionModel) {
            @Override
            public void draw(Canvas canvas, Paint paint, PixelConverter converter) {
                paint.reset();
                paint.setColor(selectionModel.getBallColor());
                canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, converter.pxFromDp(selectionModel.getBallR()), paint);
            }
        });
        imageView.setPixelConverter(converter);

        Button btn = (Button) findViewById(R.id.buttonSelectBall);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionModel.setSelectedObject(SelectionModel.BALL);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        SeekBar radius = (SeekBar) findViewById(R.id.ballRadiusSeeker);
        radius.setMax(20);
        radius.setProgress((int) selectionModel.getBallR() - 10);
        seekBarDrawerHack(radius);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    selectionModel.setBallR(seekBar.getProgress() + 10);
                    imageView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar color = (SeekBar) findViewById(R.id.ballColorSeeker);
        initColorPicker(color);
        color.setProgress(selectionModel.getBallColorProgress());
        seekBarDrawerHack(color);
        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectionModel.setBallColorProgress(i);
                selectionModel.setBallColor(getColorFromProgress(i));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void seekBarDrawerHack(SeekBar seekBar) {
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow Drawer to intercept touch events.
                        view.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow Drawer to intercept touch events.
                        view.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void initColorPicker(SeekBar colorPicker) {
        LinearGradient test = new LinearGradient(0.f, 0.f, 180f * getResources().getDisplayMetrics().density - 70, 0.0f,
                new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(test);

        colorPicker.setProgressDrawable((Drawable) shape);
        colorPicker.setMax(256 * 7 - 1);
    }

    private int getColorFromProgress(int progress) {
        int r = 0;
        int g = 0;
        int b = 0;

        if (progress < 256) {
            b = progress;
        } else if (progress < 256 * 2) {
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 3) {
            g = 255;
            b = progress % 256;
        } else if (progress < 256 * 4) {
            r = progress % 256;
            g = 256 - progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 5) {
            r = 255;
            g = 0;
            b = progress % 256;
        } else if (progress < 256 * 6) {
            r = 255;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 7) {
            r = 255;
            g = 255;
            b = progress % 256;
        }
        return Color.argb(255, r, g, b);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getX() > 50 && motionEvent.getY() < view.getHeight() - 20) {
                    switch (selectionModel.getSelectedObject()) {
                        case SelectionModel.BALL:
                            model.placeBall(converter.dpFromPx(motionEvent.getX()), converter.dpFromPx(motionEvent.getY()), selectionModel.getBallR(), selectionModel.getBallColor());
                            break;
                        case SelectionModel.GOAL:
                            model.placeGoal(converter.dpFromPx(motionEvent.getX()), converter.dpFromPx(motionEvent.getY()), selectionModel.getGoalRadius(), selectionModel.getGoalColorPrimary(), selectionModel.getGoalColorSecondary());
                            break;
                        case SelectionModel.OBSTACLE:
                            model.placeObstacle(converter.dpFromPx(motionEvent.getX()), converter.dpFromPx(motionEvent.getY()), selectionModel.getObstacleColor());
                            break;
                        case SelectionModel.TRAP:
                            model.placeTrap(converter.dpFromPx(motionEvent.getX()), converter.dpFromPx(motionEvent.getY()), selectionModel.getTrapRadius(), selectionModel.getTrapColorPrimary(), selectionModel.getTrapColorSecondary());
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                model.move(converter.dpFromPx(motionEvent.getX()), converter.dpFromPx(motionEvent.getY()));
                break;
            case MotionEvent.ACTION_UP:
                model.save();
                break;
        }
        return true;
    }

    @Override
    public void onMessage(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }
}

