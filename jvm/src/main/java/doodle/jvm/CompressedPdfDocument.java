package doodle.jvm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.erichseifert.vectorgraphics2d.GraphicsState;
import de.erichseifert.vectorgraphics2d.SizedDocument;
import de.erichseifert.vectorgraphics2d.intermediate.commands.AffineTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.CreateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DisposeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawImageCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawStringCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.FillShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Group;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetBackgroundCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetClipCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetFontCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetHintCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetPaintCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetStrokeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.util.DataUtils;
import de.erichseifert.vectorgraphics2d.util.FlateEncodeStream;
import de.erichseifert.vectorgraphics2d.util.FormattingWriter;
import de.erichseifert.vectorgraphics2d.util.GraphicsUtils;
import de.erichseifert.vectorgraphics2d.util.ImageDataStream;
import de.erichseifert.vectorgraphics2d.util.ImageDataStream.Interleaving;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import de.erichseifert.vectorgraphics2d.pdf.*;


// Copied from https://raw.githubusercontent.com/eseifert/vectorgraphics2d/master/src/main/java/de/erichseifert/vectorgraphics2d/pdf/PDFDocument.java
//
// Turning on compression
public class CompressedPdfDocument extends SizedDocument {
	private static final String EOL = "\n";
	private static final String CHARSET = "ISO-8859-1";
	private static final String HEADER = "%PDF-1.4";
	private static final String FOOTER = "%%EOF";

	/** Constant to convert values from millimeters to PostScript®/PDF units (1/72th inch). */
	private static final double MM_IN_UNITS = 72.0/25.4;

	/** Mapping of stroke endcap values from Java to PDF. */
	private static final Map<Integer, Integer> STROKE_ENDCAPS = DataUtils.map(
		new Integer[] { BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE },
		new Integer[] { 0, 1, 2 }
	);

	/** Mapping of line join values for path drawing from Java to PDF. */
	private static final Map<Integer, Integer> STROKE_LINEJOIN = DataUtils.map(
		new Integer[] { BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL },
		new Integer[] { 0, 1, 2 }
	);

	private final List<PDFObject> objects;
	private int objectIdCounter;
	private final Map<PDFObject, Long> xref;

	private PDFObject contents;
	private Resources resources;
	private final Map<Integer, PDFObject> images;

	private final Stack<GraphicsState> states;
	private boolean transformed;

	private boolean compressed;

	public CompressedPdfDocument(PageSize pageSize) {
		super(pageSize);

		states = new Stack<GraphicsState>();
		states.push(new GraphicsState());

		objects = new LinkedList<PDFObject>();
		objectIdCounter = 1;
		xref = new HashMap<PDFObject, Long>();
		images = new HashMap<Integer, PDFObject>();

    compressed = true;

		initPage();
	}

	private GraphicsState getCurrentState() {
		return states.peek();
	}

	private void initPage() {
		Map<String, Object> dict;

		// Catalog
		dict = DataUtils.map(
			new String[] {"Type"},
			new Object[] {"Catalog"}
		);
		PDFObject catalog = addObject(dict, null);

		// Pages
		List<PDFObject> pagesKids = new LinkedList<PDFObject>();
		dict = DataUtils.map(
			new String[] {"Type", "Kids", "Count"},
			new Object[] {"Pages", pagesKids, 1}
		);
		PDFObject pages = addObject(dict, null);
		catalog.dict.put("Pages", pages);

		// Page
		double x = getPageSize().x*MM_IN_UNITS;
		double y = getPageSize().y*MM_IN_UNITS;
		double width = getPageSize().width*MM_IN_UNITS;
		double height = getPageSize().height*MM_IN_UNITS;
		dict = DataUtils.map(
			new String[] {"Type", "Parent", "MediaBox"},
			new Object[] {"Page", pages, new double[] {x, y, width, height}}
		);
		PDFObject page = addObject(dict, null);
		pagesKids.add(page);

		// Contents
		Payload contentsPayload = new Payload(true);
		contents = addObject(null, contentsPayload);
		page.dict.put("Contents", contents);

		// Compression
		if (compressed) {
			contentsPayload.addFilter(FlateEncodeStream.class);
			contents.dict.put("Filter", new Object[] {"FlateDecode"});
		}

		// Initial content
		try {
			contentsPayload.write(DataUtils.join("", new Object[] {
				"q", EOL,
				getOutput(getCurrentState().getColor()), EOL,
				MM_IN_UNITS, " 0 0 ", -MM_IN_UNITS, " 0 ", height, " cm", EOL
			}).getBytes(CHARSET));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Content length
		Payload contentLengthPayload = new SizePayload(contents, CHARSET, false);
		PDFObject contentLength = addObject(null, contentLengthPayload);
		contents.dict.put("Length", contentLength);

		// Resources
		resources = new Resources(objectIdCounter++, 0);
		objects.add(resources);
		page.dict.put("Resources", resources);

		// Create initial font
		Font font = getCurrentState().getFont();
		String fontResourceId = resources.getId(font);
		float fontSize = font.getSize2D();
		StringBuilder out = new StringBuilder();
		out.append("/").append(fontResourceId).append(" ").append(fontSize).append(" Tf").append(EOL);
		try {
			contentsPayload.write(
				out.toString().getBytes(CHARSET)
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private PDFObject addObject(Map<String, Object> dict, Payload payload) {
		final int id = objectIdCounter++;
		final int version = 0;
		PDFObject object = new PDFObject(id, version, dict, payload);
		objects.add(object);
		return object;
	}

	private PDFObject addObject(Image image) {
		BufferedImage bufferedImage = GraphicsUtils.toBufferedImage(image);

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int bitsPerSample = DataUtils.max(bufferedImage.getSampleModel().getSampleSize());
		int bands = bufferedImage.getSampleModel().getNumBands();
		String colorSpaceName = (bands == 1) ? "DeviceGray" : "DeviceRGB";

		Payload imagePayload = new Payload(true);

		// Compression
		String[] imageFilters = {};
		if (compressed) {
			imagePayload.addFilter(FlateEncodeStream.class);
			imageFilters = new String[] {"FlateDecode"};
		}

		InputStream imageDataStream =
				new ImageDataStream(bufferedImage, Interleaving.WITHOUT_ALPHA);

		try {
			DataUtils.transfer(imageDataStream, imagePayload, 1024);
			imagePayload.close();
		} catch (IOException e) {
			// TODO Improve exception handling
			throw new RuntimeException(e);
		}

		int length = imagePayload.getBytes().length;

		Map<String, Object> imageDict = DataUtils.map(
			new String[] {"Type", "Subtype", "Width", "Height", "ColorSpace",
					"BitsPerComponent", "Length", "Filter"},
			new Object[] {"XObject", "Image", width, height, colorSpaceName,
					bitsPerSample, length, imageFilters}
		);

		PDFObject imageObject = addObject(imageDict, imagePayload);

		boolean hasAlpha = bufferedImage.getColorModel().hasAlpha();
		if (hasAlpha) {
			BufferedImage mask = GraphicsUtils.getAlphaImage(bufferedImage);

			PDFObject maskObject = addObject(mask);

			boolean isBitmask = mask.getSampleModel().getSampleSize(0) == 1;
			if (isBitmask) {
				maskObject.dict.put("ImageMask", true);
				maskObject.dict.remove("ColorSpace");
				imageObject.dict.put("Mask", maskObject);
			} else {
				imageObject.dict.put("SMask", maskObject);
			}
		}

		return imageObject;
	}

	public void write(OutputStream out) throws IOException {
		FormattingWriter o = new FormattingWriter(out, CHARSET, EOL);

		o.writeln(HEADER);

		for (PDFObject obj : objects) {
			xref.put(obj, o.tell());
			o.writeln(toString(obj));
			o.flush();
		}

		long xrefPos = o.tell();
		o.writeln("xref");
		o.write(0).write(" ").writeln(objects.size() + 1);
		o.format("%010d %05d f ", 0, 65535).writeln();
		for (PDFObject obj : objects) {
			o.format("%010d %05d n ", xref.get(obj), 0).writeln();
		}
		o.flush();

		o.writeln("trailer");
		o.writeln(serialize(DataUtils.map(
			new String[] {"Size", "Root"},
			new Object[] {objects.size() + 1, objects.get(0)}
		)));

		o.writeln("startxref");
		o.writeln(xrefPos);

		o.writeln(FOOTER);
		o.flush();
	}

	public static String toString(PDFObject obj) {
		StringBuilder out = new StringBuilder();
		out.append(obj.id).append(" ").append(obj.version).append(" obj")
			.append(EOL);
		if (!obj.dict.isEmpty()) {
			out.append(serialize(obj.dict)).append(EOL);
		}
		if (obj.payload != null) {
			String content;
			try {
				content = new String(obj.payload.getBytes(), CHARSET);
			} catch (UnsupportedEncodingException e) {
				content = "";
			}
			if (content.length() > 0) {
				if (obj.payload.isStream()) {
					out.append("stream").append(EOL);
				}
				out.append(content);
				if (obj.payload.isStream()) {
					out.append("endstream");
				}
				out.append(EOL);
			}
		}
		out.append("endobj");
		return out.toString();
	}

	private static String serialize(Object obj) {
		if (obj instanceof String) {
			return "/" + obj.toString();
		} else if (obj instanceof float[]) {
			return serialize(DataUtils.asList((float[]) obj));
		} else if (obj instanceof double[]) {
			return serialize(DataUtils.asList((double[]) obj));
		} else if (obj instanceof Object[]) {
			return serialize(Arrays.asList((Object[]) obj));
		} else if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			StringBuilder out = new StringBuilder();
			out.append("[");
			int i = 0;
			for (Object elem : list) {
				if (i++ > 0) {
					out.append(" ");
				}
				out.append(serialize(elem));
			}
			out.append("]");
			return out.toString();
		} else if (obj instanceof Map) {
			Map<?, ?> dict = (Map<?, ?>) obj;
			StringBuilder out = new StringBuilder();
			out.append("<<").append(EOL);
			for (Map.Entry<?, ?> entry : dict.entrySet()) {
				String key = entry.getKey().toString();
				out.append(serialize(key)).append(" ");

				Object value = entry.getValue();
				out.append(serialize(value)).append(EOL);
			}
			out.append(">>");
			return out.toString();
		} else if (obj instanceof PDFObject) {
			PDFObject pdfObj = (PDFObject) obj;
			return String.valueOf(pdfObj.id) + " " + pdfObj.version + " R";
		} else {
			return DataUtils.format(obj);
		}
	}

	public void handle(Command<?> command) {
		String s = "";
		if (command instanceof Group) {
			Group c = (Group) command;
			applyStateCommands(c.getValue());
			s = getOutput(getCurrentState(), resources, !transformed);
			transformed = true;
		} else if (command instanceof DrawShapeCommand) {
			DrawShapeCommand c = (DrawShapeCommand) command;
			s = getOutput(c.getValue()) + " S";
		} else if (command instanceof FillShapeCommand) {
			FillShapeCommand c = (FillShapeCommand) command;
			s = getOutput(c.getValue()) + " f";
		} else if (command instanceof DrawStringCommand) {
			DrawStringCommand c = (DrawStringCommand) command;
			s = getOutput(c.getValue(), c.getX(), c.getY());
		} else if (command instanceof DrawImageCommand) {
			DrawImageCommand c = (DrawImageCommand) command;
			// Create object for image data
			Image image = c.getValue();
			PDFObject imageObject = images.get(image.hashCode());
			if (imageObject == null) {
				imageObject = addObject(image);
				images.put(image.hashCode(), imageObject);
			}
			s = getOutput(imageObject, c.getX(), c.getY(),
					c.getWidth(), c.getHeight(), resources);
		}

		try {
			Payload contentsPayload = contents.payload;
			contentsPayload.write(s.getBytes(CHARSET));
			contentsPayload.write(EOL.getBytes(CHARSET));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void applyStateCommands(List<Command<?>> commands) {
		for (Command<?> command : commands) {
			if (command instanceof SetHintCommand) {
				SetHintCommand c = (SetHintCommand) command;
				getCurrentState().getHints().put(c.getKey(), c.getValue());
			} else if (command instanceof SetBackgroundCommand) {
				SetBackgroundCommand c = (SetBackgroundCommand) command;
				getCurrentState().setBackground(c.getValue());
			} else if (command instanceof SetColorCommand) {
				SetColorCommand c = (SetColorCommand) command;
				getCurrentState().setColor(c.getValue());
			} else if (command instanceof SetPaintCommand) {
				SetPaintCommand c = (SetPaintCommand) command;
				getCurrentState().setPaint(c.getValue());
			} else if (command instanceof SetStrokeCommand) {
				SetStrokeCommand c = (SetStrokeCommand) command;
				getCurrentState().setStroke(c.getValue());
			} else if (command instanceof SetFontCommand) {
				SetFontCommand c = (SetFontCommand) command;
				getCurrentState().setFont(c.getValue());
			} else if (command instanceof SetTransformCommand) {
				throw new UnsupportedOperationException("The PDF format has no means of setting the transformation matrix.");
			} else if (command instanceof AffineTransformCommand) {
				AffineTransformCommand c = (AffineTransformCommand) command;
				AffineTransform stateTransform = getCurrentState().getTransform();
				AffineTransform transformToBeApplied = c.getValue();
				stateTransform.concatenate(transformToBeApplied);
				getCurrentState().setTransform(stateTransform);
			} else if (command instanceof SetClipCommand) {
				SetClipCommand c = (SetClipCommand) command;
				getCurrentState().setClip(c.getValue());
			} else if (command instanceof CreateCommand) {
				try {
					states.push((GraphicsState) getCurrentState().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			} else if (command instanceof DisposeCommand) {
				states.pop();
			}
		}
	}

	private static String getOutput(Color color) {
		if (color.getColorSpace().getType() == ColorSpace.TYPE_CMYK) {
			float[] cmyk = color.getComponents(null);
			String c = serialize(cmyk[0]);
			String m = serialize(cmyk[1]);
			String y = serialize(cmyk[2]);
			String k = serialize(cmyk[3]);
			return c + " " + m + " " + y + " " + k + " k " +
					c + " " + m + " " + y + " " + k + " K";
		} else {
			String r = serialize(color.getRed()/255.0);
			String g = serialize(color.getGreen()/255.0);
			String b = serialize(color.getBlue()/255.0);
			return r + " " + g + " " + b + " rg " +
					r + " " + g + " " + b + " RG";
		}
	}

	private static String getOutput(Shape s) {
		StringBuilder out = new StringBuilder();
		PathIterator segments = s.getPathIterator(null);
		double[] coordsCur = new double[6];
		double[] pointPrev = new double[2];
		for (int i = 0; !segments.isDone(); i++, segments.next()) {
			if (i > 0) {
				out.append(" ");
			}
			int segmentType = segments.currentSegment(coordsCur);
			switch (segmentType) {
			case PathIterator.SEG_MOVETO:
				out.append(serialize(coordsCur[0])).append(" ")
					.append(serialize(coordsCur[1])).append(" m");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_LINETO:
				out.append(serialize(coordsCur[0])).append(" ")
					.append(serialize(coordsCur[1])).append(" l");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_CUBICTO:
				out.append(serialize(coordsCur[0])).append(" ")
					.append(serialize(coordsCur[1])).append(" ")
					.append(serialize(coordsCur[2])).append(" ")
					.append(serialize(coordsCur[3])).append(" ")
					.append(serialize(coordsCur[4])).append(" ")
					.append(serialize(coordsCur[5])).append(" c");
				pointPrev[0] = coordsCur[4];
				pointPrev[1] = coordsCur[5];
				break;
			case PathIterator.SEG_QUADTO:
				double x1 = pointPrev[0] + 2.0/3.0*(coordsCur[0] - pointPrev[0]);
				double y1 = pointPrev[1] + 2.0/3.0*(coordsCur[1] - pointPrev[1]);
				double x2 = coordsCur[0] + 1.0/3.0*(coordsCur[2] - coordsCur[0]);
				double y2 = coordsCur[1] + 1.0/3.0*(coordsCur[3] - coordsCur[1]);
				double x3 = coordsCur[2];
				double y3 = coordsCur[3];
				out.append(serialize(x1)).append(" ")
					.append(serialize(y1)).append(" ")
					.append(serialize(x2)).append(" ")
					.append(serialize(y2)).append(" ")
					.append(serialize(x3)).append(" ")
					.append(serialize(y3)).append(" c");
				pointPrev[0] = x3;
				pointPrev[1] = y3;
				break;
			case PathIterator.SEG_CLOSE:
				out.append("h");
				break;
			default:
				throw new IllegalStateException("Unknown path operation.");
			}
		}

		return out.toString();
	}

	private static String getOutput(GraphicsState state, Resources resources, boolean first) {
		StringBuilder out = new StringBuilder();

		if (!first) {
			out.append("Q").append(EOL);
		}
		out.append("q").append(EOL);

		if (!state.getColor().equals(GraphicsState.DEFAULT_COLOR)) {
			if (state.getColor().getAlpha() != GraphicsState.DEFAULT_COLOR.getAlpha()) {
				double a = state.getColor().getAlpha()/255.0;
				String resourceId = resources.getId(a);
				out.append("/").append(resourceId).append(" gs").append(EOL);
			}
			out.append(getOutput(state.getColor())).append(EOL);
		}
		if (!state.getTransform().equals(GraphicsState.DEFAULT_TRANSFORM)) {
			out.append(getOutput(state.getTransform())).append(" cm").append(EOL);
		}
		if (!state.getStroke().equals(GraphicsState.DEFAULT_STROKE)) {
			out.append(getOutput(state.getStroke())).append(EOL);
		}
		if (state.getClip() != GraphicsState.DEFAULT_CLIP) {
			out.append(getOutput(state.getClip())).append(" W n").append(EOL);
		}
		if (!state.getFont().equals(GraphicsState.DEFAULT_FONT)) {
			Font font = state.getFont();
			String fontResourceId = resources.getId(font);
			float fontSize = font.getSize2D();
			out.append("/").append(fontResourceId).append(" ").append(fontSize)
				.append(" Tf").append(EOL);
		}

		return DataUtils.stripTrailing(out.toString(), EOL);
	}

	private static String getOutput(Stroke s) {
		StringBuilder out = new StringBuilder();
		if (s instanceof BasicStroke) {
			BasicStroke strokeDefault = (BasicStroke) GraphicsState.DEFAULT_STROKE;
			BasicStroke strokeNew = (BasicStroke) s;
			if (strokeNew.getLineWidth() != strokeDefault.getLineWidth()) {
				out.append(serialize(strokeNew.getLineWidth()))
					.append(" w").append(EOL);
			}
			if (strokeNew.getLineJoin() == BasicStroke.JOIN_MITER && strokeNew.getMiterLimit() != strokeDefault.getMiterLimit()) {
				out.append(serialize(strokeNew.getMiterLimit()))
					.append(" M").append(EOL);
			}
			if (strokeNew.getLineJoin() != strokeDefault.getLineJoin()) {
				out.append(serialize(STROKE_LINEJOIN.get(strokeNew.getLineJoin())))
					.append(" j").append(EOL);
			}
			if (strokeNew.getEndCap() != strokeDefault.getEndCap()) {
				out.append(serialize(STROKE_ENDCAPS.get(strokeNew.getEndCap())))
					.append(" J").append(EOL);
			}
			if (strokeNew.getDashArray() != strokeDefault.getDashArray()) {
				if (strokeNew.getDashArray() != null) {
					out.append(serialize(strokeNew.getDashArray())).append(" ")
						.append(serialize(strokeNew.getDashPhase()))
						.append(" d").append(EOL);
				} else {
					out.append(EOL).append("[] 0 d").append(EOL);
				}
			}
		}
		return out.toString();
	}

	private static String getOutput(AffineTransform transform) {
		double[] matrix = new double[6];
		transform.getMatrix(matrix);
		return DataUtils.join(" ", matrix);
	}

	private static String getOutput(String str, double x, double y) {

		// Save current graphics state
		// Undo swapping of y axis
		// Render text
		// Restore previous graphics state

		return "q " + "1 0 0 -1 " + x + " " + y + " cm " + "BT " + getOutput(str) + " Tj ET " + "Q";
	}

	private static StringBuilder getOutput(String str) {
		StringBuilder out = new StringBuilder();

		// Escape string
		str = str.replaceAll("\\\\", "\\\\\\\\")
			.replaceAll("\t", "\\\\t")
			.replaceAll("\b", "\\\\b")
			.replaceAll("\f", "\\\\f")
			.replaceAll("\\(", "\\\\(")
			.replaceAll("\\)", "\\\\)")
			.replaceAll("[\r\n]", "");

		out.append("(").append(str).append(")");

		return out;
	}

	private static String getOutput(PDFObject image, double x, double y,
			double width, double height, Resources resources) {
		// Query image resource id
		String resourceId = resources.getId(image);

		// Save graphics state
		// Move image to correct position and scale it to (width, height)
		// Swap y axis
		// Draw image
		// Restore old graphics state

		return "q " + width + " 0 0 " + height + " " + x + " " + y + " cm " + "1 0 0 -1 0 1 cm " + "/" + resourceId + " Do " + "Q";
	}

	@Override
	public void close() {
		try {
			String footer = "Q" + EOL;
			if (transformed) {
				footer += "Q" + EOL;
			}
			Payload contentsPayload = contents.payload;
			contentsPayload.write(footer.getBytes(CHARSET));
			contentsPayload.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.close();
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}
}
