package com.soebes.performance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author Karl Heinz Marbaise <a href="mailto">khmarbaise@apache.org</a>
 */
@Fork( 5 )
@Warmup( iterations = 10 )
@Measurement( iterations = 100 )
@State( Scope.Benchmark )
@BenchmarkMode( Mode.AverageTime )
public class DefaultModelValidatorPerformance
{
    public static final String SHA1_PROPERTY = "sha1";

    public static final String CHANGELIST_PROPERTY = "changelist";

    public static final String REVISION_PROPERTY = "revision";

    public static final String SHA1_PROPERTY_EXPRESSION = "${" + SHA1_PROPERTY + "}";

    public static final String CHANGELIST_PROPERTY_EXPRESSION = "${" + CHANGELIST_PROPERTY + "}";

    public static final String REVISION_PROPERTY_EXPRESSION = "${" + REVISION_PROPERTY + "}";

    private static final Pattern PATTERN_SHA1_PROPERTY = Pattern.compile( SHA1_PROPERTY_EXPRESSION, Pattern.LITERAL );

    private static final Pattern PATTERN_CHANGELIST_PROPERTY =
        Pattern.compile( CHANGELIST_PROPERTY_EXPRESSION, Pattern.LITERAL );

    private static final Pattern PATTERN_REVISION_PROPERTY =
        Pattern.compile( REVISION_PROPERTY_EXPRESSION, Pattern.LITERAL );

    public static final String[] EXPRESSIONS =
        { "${changelist}-${wrong}", "${revision}${changelist}${sha1}", "${wrong}", "${wrong}${sha1}${changelist}" };

    @Benchmark
    public void testSearchAndReplace()
    {
        for ( int round = 0; round < 100000; round++ )
        {
            for ( int i = 0; i < EXPRESSIONS.length; i++ )
            {
                boolean result = v1( EXPRESSIONS[i] );
            }
        }
    }

    @Benchmark
    public void testSearchViaRegExGroups()
    {
        for ( int round = 0; round < 100000; round++ )
        {
            for ( int i = 0; i < EXPRESSIONS.length; i++ )
            {
                boolean result = v2( EXPRESSIONS[i] );
            }
        }
    }

    @Benchmark
    public void testViaDifferentRegEx()
    {
        for ( int round = 0; round < 100000; round++ )
        {
            for ( int i = 0; i < EXPRESSIONS.length; i++ )
            {
                boolean result = v3( EXPRESSIONS[i] );
            }
        }
    }

    public boolean v1( String string )
    {
        //@formatter:off
        string =
            PATTERN_REVISION_PROPERTY
                .matcher( string )
                .replaceAll( REVISION_PROPERTY );
        string =
            PATTERN_CHANGELIST_PROPERTY
                .matcher( string )
                .replaceAll( CHANGELIST_PROPERTY );
        string =
            PATTERN_SHA1_PROPERTY
                .matcher( string )
                .replaceAll( SHA1_PROPERTY );
        //@formatter:on
        if ( string.contains( "${" ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean v2( String a )
    {
        Pattern X = Pattern.compile( "\\$\\{(\\w+)\\}" );
        Matcher matcher = X.matcher( a );
        Set<String> result = new HashSet<>();
        while ( matcher.find() )
        {
            result.add( matcher.group( 1 ) );
        }

        if ( result.size() == 3 )
        {
            return result.contains( CHANGELIST_PROPERTY ) && result.contains( REVISION_PROPERTY )
                && result.contains( SHA1_PROPERTY );
        }
        else
        {
            return result.contains( CHANGELIST_PROPERTY ) || result.contains( REVISION_PROPERTY )
                || result.contains( SHA1_PROPERTY );
        }
    }

    public boolean v3( String a )
    {
        Pattern p = Pattern.compile( "\\$\\{(.+?)\\}" );

        List<String> ciVersions = Arrays.asList( REVISION_PROPERTY, CHANGELIST_PROPERTY, SHA1_PROPERTY );
        Matcher m = p.matcher( a.trim() );
        while ( m.find() )
        {
            if ( !ciVersions.contains( m.group( 1 ) ) )
            {
                return false;
            }
        }
        
        return true;
    }
}
