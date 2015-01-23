package org.exist.xquery.modules.dita;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.exist.dom.QName;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A function to run DITA OT processing.
 *
 * @author Ivan Lagunov
 */
public class RunDitaOTFunction extends BasicFunction {

    public static final String UTF_8 = "UTF-8";
    public final static String DITA_HOME = "DITA_HOME";
    public final static String DITA_DIR = System.getenv(DITA_HOME);
    public final static String DITA_EXECUTABLE = DITA_DIR + File.separatorChar + "bin" + File.separatorChar +
            "dita" + (SystemUtils.IS_OS_WINDOWS ? ".bat" : "");

    public final static FunctionSignature signature = new FunctionSignature(
            new QName("run-dita-ot", DitaModule.NAMESPACE_URI, DitaModule.PREFIX),
            "A function to run DITA OT processing.",
            new SequenceType[]{
                    new FunctionParameterSequenceType("parameters", Type.STRING, Cardinality.ZERO_OR_MORE,
                            "The parameters in format 'ant-parameter-name=value'. " +
                                    "The documentation is available here: http://www.dita-ot.org/2.0/readme/dita-ot_ant_properties.html"
                    )
            },
            new SequenceType(Type.ITEM, Cardinality.EMPTY)
    );

    public RunDitaOTFunction(XQueryContext context) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] sequences, Sequence sequence) throws XPathException {
        return staticEval(sequences, sequence);
    }

    protected static Sequence staticEval(Sequence[] sequences, Sequence sequence) throws XPathException {
        if (null == DITA_DIR) {
            String errorMessage = DITA_HOME + " environmental variable is not found";
            LOG.fatal(errorMessage);
            throw new XPathException(errorMessage);
        }

        LOG.info("Running DITA OT with parameters: " + Arrays.toString(sequences));
        ProcessBuilder pb = new ProcessBuilder(getDitaCommand(sequences)).directory(new File(DITA_DIR));
        try {
            Process process = pb.start();
            LOG.debug(IOUtils.toString(process.getInputStream(), UTF_8));
            LOG.error(IOUtils.toString(process.getErrorStream(), UTF_8));
            LOG.info("Completed DITA OT processing" );
        } catch (IOException e) {
            LOG.error("DITA OT process failed", e);
        }

        return Sequence.EMPTY_SEQUENCE;
    }

    private static String[] getDitaCommand(Sequence[] sequences) throws XPathException {
        List<String> args = new ArrayList<>();

        args.add(DITA_EXECUTABLE);
        if (!sequences[0].isEmpty()) {
            SequenceIterator iterator = sequences[0].iterate();
            while (iterator.hasNext()) {
                args.add(iterator.nextItem().getStringValue());
            }
        }

        return args.toArray(new String[args.size()]);
    }
}
