package com.soebes.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DefaultModelValidatorPerformanceV1Test
{
    @Parameters
    public static Collection<Object[]> data() {
        
        return Arrays.asList(new Object[][] {     
                 { "${revision}${changelist}${sha1}", true }, 
                 { "${revision}${changelist}", true }, 
                 { "${revision}", true }, 
                 { "${changelist}", true }, 
                 { "${sha1}", true }, 
                 { "${changelist}-${wrong}", false },
                 { "${wrong}", false },
                 { "${wrong}${sha1}${changelist}", false },
           });
    }

    private String input;
    private boolean expectedResult;

    
    public DefaultModelValidatorPerformanceV1Test( String input, boolean expectedResult )
    {
        super();
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Test
    public void testV1()
    {
        DefaultModelValidatorPerformance d = new DefaultModelValidatorPerformance();
        assertThat( d.v1( input ) ).as( "input='%s'", input, expectedResult ).isEqualTo( expectedResult );
    }

}
