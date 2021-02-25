package com.epam.deltix.gflog.benchmark.gflog;

import com.epam.deltix.gflog.benchmark.util.BenchmarkState;
import net.openhft.affinity.Affinity;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.ThreadParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static com.epam.deltix.gflog.benchmark.util.BenchmarkUtil.*;

@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 15)
@Fork(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Threads(1)
public class GflogThroughputBenchmark {

    // 100 bytes + (line separator 1-2)
    // 2020-10-01 14:25:58.310 INFO '123456789012345678901234567890' [12345678901234567890123] Hello world!

    @Param({
            "noop",
            /*"console-direct",
            "console-wrapper",*/
            "file",
            "rolling-file"
    })
    public String config;

    @Param({"ASCII", "UTF-8"})
    public String encoding;

    @Setup
    public void prepare() {
        GflogBenchmarkUtil.prepare(config, encoding);
    }

    @TearDown
    public void cleanup() {
        GflogBenchmarkUtil.cleanup();
    }

    @Benchmark
    public void baseline(final ThreadState state) {
    }

    @Benchmark
    public void timestamp(final ThreadState state) {
        System.currentTimeMillis();
    }

    @Benchmark
    public void entry1Arg(final ThreadState state) {
        GflogBenchmarkUtil.entry1Arg(state);
    }

    @Benchmark
    public void template1Arg(final ThreadState state) {
        GflogBenchmarkUtil.template1Arg(state);
    }

    @Benchmark
    public void entry5Args(final ThreadState state) {
        GflogBenchmarkUtil.entry5Args(state);
    }

    @Benchmark
    public void template5Args(final ThreadState state) {
        GflogBenchmarkUtil.template5Args(state);
    }

    @Benchmark
    public void entry10Args(final ThreadState state) {
        GflogBenchmarkUtil.entry10Args(state);
    }

    @Benchmark
    public void template10Args(final ThreadState state) {
        GflogBenchmarkUtil.template10Args(state);
    }

    public static void main(final String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(GflogThroughputBenchmark.class.getName())
                // .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class ThreadState extends BenchmarkState {

        @Setup
        public void setup(final ThreadParams params) {
            Thread.currentThread().setName(THREAD);

            if (AFFINITY_BASE >= 0 && AFFINITY_STEP >= 0) {
                final int index = params.getThreadIndex();
                final int affinity = AFFINITY_BASE + index * AFFINITY_STEP;

                Affinity.setAffinity(affinity);
            }
        }

    }

}