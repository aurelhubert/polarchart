package com.aurelhubert.polarchart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class PolarChart extends View implements View.OnTouchListener {

	// TODO: Compare nbSections & array of values size
	// TODO: Save values when rotating screen

	private final String TAG = "PolarChart";

	// Settings
	private PolarChartListener polarChartListener;
	private int nbSections = 8;
	private int nbCircles = 4;
	private boolean useBezierCurve = true;
	private int graphBezierFactor = 8;

	//
	private float density;
	private float radius;
	private int chartWidth, chartHeight, currentPathX, currentPathY, currentSectionTouched = -1;
	private float currentAngle, currentSectionX, currentSectionY;
	private ArrayList<Float> sectionsValue = new ArrayList<>();
	private float padding = 0;
	private Path graph = new Path();
	private ArrayList<Path> sectionsPath = new ArrayList<>();

	//
	private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint sectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/**
	 * Constructor
	 */
	public PolarChart(Context context) {
		super(context);
		init(context, null);
	}

	/**
	 * Constructor
	 */
	public PolarChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 * Constructor
	 */
	public PolarChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	/**
	 * Constructor
	 */
	@TargetApi(21)
	public PolarChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		chartWidth = w;
		chartHeight = h;

		if (chartWidth > chartHeight) {
			radius = chartHeight * nbCircles * 1f / (nbCircles * 2) - padding;
		} else {
			radius = chartWidth * nbCircles * 1f / (nbCircles * 2) - padding;
		}

		createSectionsPath();
		createGraphPath();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Draw values
		canvas.drawPath(graph, shapePaint);

		// Draw circles
		for (int i = 1; i <= nbCircles; i++) {
			canvas.drawCircle(chartWidth / 2, chartHeight / 2,
					(radius * i * 1f) / (nbCircles), circlePaint);
		}

		// Draw sectionsPath
		for (Path sectionPath : sectionsPath) {
			canvas.drawPath(sectionPath, sectionPaint);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		manageTouch(event);
		return true;
	}

	/**
	 * Init
	 *
	 * @param context
	 */
	private void init(Context context, AttributeSet attrs) {

		if (!isInEditMode() && attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PolarChart);
			nbSections = a.getInt(R.styleable.PolarChart_nb_sections, 8);
			nbCircles = a.getInt(R.styleable.PolarChart_nb_circles, 5);
		}

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		density = metrics.density;
		padding = 10 * density;

		setBackgroundColor(Color.TRANSPARENT);

		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setColor(Color.parseColor("#212121"));
		circlePaint.setStrokeWidth(1);
		circlePaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 2));

		sectionPaint.setStyle(Paint.Style.STROKE);
		sectionPaint.setColor(Color.parseColor("#212121"));
		sectionPaint.setStrokeWidth(1);
		sectionPaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 2));

		shapePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		shapePaint.setColor(Color.parseColor("#2196F3"));
	}

	/**
	 * Create sections path
	 */
	private void createSectionsPath() {

		sectionsPath.clear();

		for (int i = 0; i < nbSections; i++) {
			currentAngle = -90 + (i * 1f / nbSections * 360);
			float centerX = (float) ((chartWidth / 2) + Math.cos(currentAngle * Math.PI / 180));
			float centerY = (float) ((chartHeight / 2) + Math.sin(currentAngle * Math.PI / 180));
			currentSectionX = (float) ((chartWidth / 2) + radius * Math.cos(currentAngle * Math.PI / 180));
			currentSectionY = (float) ((chartHeight / 2) + radius * Math.sin(currentAngle * Math.PI / 180));
			Path path = new Path();
			path.moveTo(centerX, centerY);
			path.lineTo(currentSectionX, currentSectionY);
			sectionsPath.add(path);
		}
	}

	/**
	 * Create graph path
	 */
	private void createGraphPath() {

		if (sectionsValue.size() == 0) {
			return;
		}

		graph.reset();

		if (useBezierCurve) {

			List<Point> points = new ArrayList<>();
			for (int i = 0; i < sectionsValue.size(); i++) {
				currentAngle = -90 + (i * 1f / nbSections * 360);
				currentPathX = (int) ((chartWidth / 2) + (radius * sectionsValue.get(i) * 1f / nbCircles)
						* Math.cos(currentAngle * Math.PI / 180));
				currentPathY = (int) ((chartHeight / 2) + (radius * sectionsValue.get(i) * 1f / nbCircles)
						* Math.sin(currentAngle * Math.PI / 180));
				points.add(new Point(currentPathX, currentPathY));
			}

			if (points.size() > 1) {
				for (int i = 0; i < points.size(); i++) {
					if (i >= 0) {
						Point point = points.get(i);

						if (i == 0) {
							Point next = points.get(i + 1);
							point.dx = ((next.x - point.x) / graphBezierFactor);
							point.dy = ((next.y - point.y) / graphBezierFactor);
						} else if (i == points.size() - 1) {
							Point next = points.get(0);
							Point prev = points.get(i - 1);
							point.dx = ((next.x - prev.x) / graphBezierFactor);
							point.dy = ((next.y - prev.y) / graphBezierFactor);
						} else {
							Point next = points.get(i + 1);
							Point prev = points.get(i - 1);
							point.dx = ((next.x - prev.x) / graphBezierFactor);
							point.dy = ((next.y - prev.y) / graphBezierFactor);
						}
					}
				}
				// Update initial point
				Point next = points.get(1);
				Point prev = points.get(points.size() - 1);
				points.get(0).dx = ((next.x - prev.x) / graphBezierFactor);
				points.get(0).dy = ((next.y - prev.y) / graphBezierFactor);
			}

			boolean first = true;
			for (int i = 0; i < points.size(); i++) {
				Point point = points.get(i);
				if (first) {
					first = false;
					graph.moveTo(point.x, point.y);
				} else {
					Point prev = points.get(i - 1);
					graph.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
				}
			}

			Point point = points.get(0);
			Point prev = points.get(points.size() - 1);
			graph.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);

		} else {

			for (int i = 0; i < sectionsValue.size(); i++) {
				currentAngle = -90 + (i * 1f / nbSections * 360);
				currentPathX = (int) ((chartWidth / 2) + (radius * sectionsValue.get(i) * 1f / nbCircles)
						* Math.cos(currentAngle * Math.PI / 180));
				currentPathY = (int) ((chartHeight / 2) + (radius * sectionsValue.get(i) * 1f / nbCircles)
						* Math.sin(currentAngle * Math.PI / 180));
				if (i == 0) {
					graph.moveTo(currentPathX, currentPathY);
				} else {
					graph.lineTo(currentPathX, currentPathY);
				}
			}

			currentAngle = -90 ;
			currentPathX = (int) ((chartWidth / 2) + (radius * sectionsValue.get(0) * 1f / nbCircles)
					* Math.cos(currentAngle * Math.PI / 180));
			currentPathY = (int) ((chartHeight / 2) + (radius * sectionsValue.get(0) * 1f / nbCircles)
					* Math.sin(currentAngle * Math.PI / 180));
			graph.lineTo(currentPathX, currentPathY);
			graph.close();

		}

	}

	/**
	 * Manage the touch
	 *
	 * @param event
	 */
	private void manageTouch(MotionEvent event) {

		if (polarChartListener == null) {
			return;
		}

		currentSectionX = (float) ((chartWidth / 2) + radius * Math.cos(currentAngle * Math.PI / 180));
		currentSectionY = (float) ((chartHeight / 2) + radius * Math.sin(currentAngle * Math.PI / 180));

		double theta = Math.atan2(event.getY() - (chartHeight / 2), event.getX() - (chartWidth / 2));
		theta += (nbSections / 2 + 1) * Math.PI / nbSections;
		double angle = Math.toDegrees(theta);
		if (angle < 0) {
			angle += 360;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			currentSectionTouched = (int) (angle / 360f * nbSections);
		} else if (event.getAction() == MotionEvent.ACTION_UP ||
				event.getAction() == MotionEvent.ACTION_CANCEL) {
			currentSectionTouched = -1;
			return;
		}

		float x1 = chartWidth / 2;
		float y1 = chartHeight / 2;

		double currentDistance = Math.sqrt((x1 - event.getX()) * (x1 - event.getX())
				+ (y1 - event.getY()) * (y1 - event.getY()));


		float newValue = (float) currentDistance / radius * nbCircles;
		newValue = Math.min(Math.max(newValue, 0), nbCircles);
		sectionsValue.set(currentSectionTouched, newValue);
		createGraphPath();
		invalidate();

		if (polarChartListener != null) {
			polarChartListener.onValueChanged(currentSectionTouched, newValue);
		}
	}

	////////////
	// PUBLIC //
	////////////

	/**
	 * Get the number of sectionsPath
	 */
	public int getNbSections() {
		return nbSections;
	}

	/**
	 * Set the number of sectionsPath
	 */
	public void setNbSections(int nbSections) {
		this.nbSections = nbSections;
	}

	/**
	 * Get the number of circles
	 */
	public int getNbCircles() {
		return nbCircles;
	}

	/**
	 * Set the number of circles
	 */
	public void setNbCircles(int nbCircles) {
		this.nbCircles = nbCircles;
	}

	/**
	 * Get graph bezier factor
	 */
	public int getGraphBezierFactor() {
		return graphBezierFactor;
	}

	/**
	 * Set graph bezier factor
	 */
	public void setGraphBezierFactor(int graphBezierFactor) {
		this.graphBezierFactor = graphBezierFactor;
	}

	/**
	 * Get the circle paint
	 */
	public Paint getCirclePaint() {
		return circlePaint;
	}

	/**
	 * Set the circle paint
	 */
	public void setCirclePaint(Paint circlePaint) {
		this.circlePaint = circlePaint;
		invalidate();
	}

	/**
	 * Get the section paint
	 */
	public Paint getSectionPaint() {
		return sectionPaint;
	}

	/**
	 * Set the section paint
	 */
	public void setSectionPaint(Paint sectionPaint) {
		this.sectionPaint = sectionPaint;
		invalidate();
	}

	/**
	 * Get the shape paint
	 */
	public Paint getShapePaint() {
		return shapePaint;
	}

	/**
	 * Set the shape paint
	 */
	public void setShapePaint(Paint shapePaint) {
		this.shapePaint = shapePaint;
		invalidate();
	}

	/**
	 * Return if the Bezier curve is used
	 * @return
	 */
	public boolean isUseBezierCurve() {
		return useBezierCurve;
	}

	/**
	 * Use or not the Bezier curve
	 * @param useBezierCurve
	 */
	public void setUseBezierCurve(boolean useBezierCurve) {
		this.useBezierCurve = useBezierCurve;
	}

	/**
	 * The the polar chart listenre
	 * @return
	 */
	public PolarChartListener getPolarChartListener() {
		return polarChartListener;
	}

	/**
	 * Set the polar chart listener
	 * @param polarChartListener : if null, the listener is removed
	 */
	public void setPolarChartListener(PolarChartListener polarChartListener) {
		this.polarChartListener = polarChartListener;
		if (polarChartListener == null) {
			setOnTouchListener(null);
		} else {
			setOnTouchListener(this);
		}
	}

	/**
	 * Set the sectionsPath value
	 */
	public void setSectionsValue(final ArrayList<Float> newSectionsValue, boolean animated) {

		if (newSectionsValue.size() != nbSections) {
			Log.e(TAG, "The number of values isn't equal to the number of sections");
			return;
		}

		for (int i = 0; i < newSectionsValue.size(); i++) {
			float value = newSectionsValue.get(i);
			value = Math.min(Math.max(value, 0), nbCircles);
			newSectionsValue.set(i, value);
		}

		if (animated && newSectionsValue.size() > 0 && sectionsValue.size() == newSectionsValue.size()) {

			for (int i = 0; i < newSectionsValue.size(); i++) {

				final int index = i;
				final ValueAnimator valueAnimator = ValueAnimator.ofFloat(sectionsValue.get(i), newSectionsValue.get(i));

				valueAnimator.setDuration(300);
				valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						sectionsValue.set(index, (float) animation.getAnimatedValue());
						if (index == newSectionsValue.size() - 1) {
							createGraphPath();
							invalidate();
						}
					}
				});

				valueAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
					}
				});

				valueAnimator.start();
			}
		} else {
			sectionsValue.clear();
			sectionsValue.addAll(newSectionsValue);
			createGraphPath();
			invalidate();
		}
	}


	/**
	 * Interface
	 */
	public interface PolarChartListener {
		void onValueChanged(int section, float value);

	}
}
