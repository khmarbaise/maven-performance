package com.soebes.performance;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.List;
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
@Fork( 1 )
@Warmup( iterations = 1 )
@Measurement( iterations = 1 )
@State( Scope.Benchmark )
@BenchmarkMode( Mode.AverageTime )
public class DefaultModelValidatorPerformanceTest
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

    @Benchmark
    private void testExpression()
    {
        x("${changelist}-${wrong}");
    }
    
    public void x(String string)
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
    }

    public void testName()
        throws Exception
    {
        String a = "${aal}${beta}${cbc}xyz${alpha1}";
        Pattern X = Pattern.compile( "\\$\\{(\\w+)\\}" );
        Matcher matcher = X.matcher( a );
        List<String> result = new ArrayList<>();
        while ( matcher.find() )
        {
            System.out.println( "G: '" + matcher.group() + "'" + " Count:" + matcher.groupCount() + " C:"
                + matcher.group( 1 ) );
            result.add( matcher.group( 1 ) );
        }

    }

    public void testCiFriendlyPerformanceFailure()
    {
    }

}
