#!/usr/bin/perl -d:SimpleRET_CallTrace

use threads;

$Devel::SimpleRET_Helper::disabled = 0;

sub zero {
}

&zero();


package foo;

sub bar {
	baz();
	sleep(1);
	baz();
}

sub baz { return; }

my $thr = threads->create('bar');
my $thr1 = threads->create('bar');
my $thr2 = threads->create('bar');
my $thr3 = threads->create('bar');

$thr->join();
$thr1->join();
$thr2->join();
$thr3->join();
