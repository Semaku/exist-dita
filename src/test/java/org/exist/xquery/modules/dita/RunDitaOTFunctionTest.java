package org.exist.xquery.modules.dita;

import org.exist.xquery.XPathException;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.ValueSequence;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ivan Lagunov
 */
public class RunDitaOTFunctionTest {

    @Test
    public void completeExample() throws XPathException {
        Sequence input = getRequiredArgumentsSequence();
        input.add(new StringValue("-o"));
        input.add(new StringValue("c:\\Temp\\dita\\out"));
        input.add(new StringValue("-Dargs.debug=yes"));
        input.add(new StringValue("-Dargs.logdir=c:\\Temp\\dita\\log"));
        input.add(new StringValue("-Ddita.dir=c:\\Programs\\DITA-OT"));
        input.add(new StringValue("-Dclean.temp=yes"));
        input.add(new StringValue("-Dbasedir=c:\\Temp\\dita\\base"));
        input.add(new StringValue("-temp=c:\\Temp\\dita\\temp"));
        Sequence result = RunDitaOTFunction.staticEval(new Sequence[]{ input }, null);
        Assert.assertEquals(Sequence.EMPTY_SEQUENCE, result);
    }

    @Test
    public void minimalExample() throws XPathException {
        Sequence input = getRequiredArgumentsSequence();
        Sequence result = RunDitaOTFunction.staticEval(new Sequence[]{ input }, null);
        Assert.assertEquals(Sequence.EMPTY_SEQUENCE, result);
    }

    private Sequence getRequiredArgumentsSequence() throws XPathException {
        Sequence input = new ValueSequence(true);
        input.add(new StringValue("-i"));
        input.add(new StringValue("c:\\Programs\\DITA-OT\\samples\\taskbook\\installing.dita"));
        input.add(new StringValue("-f"));
        input.add(new StringValue("pdf"));
        return input;
    }
}
