=head1 NAME

Devel::SimpleRET_CallTrace - See what your code's doing

Modified version of Devel::CallTrace 

=head1 SYNOPSIS

#!/usr/bin/perl -d:SimpleRET_CallTrace

package foo;

sub bar {
  print "bar\n";
  baz();
}

sub baz {
    print "boo\n";
}


foo::bar();

=head1 RATIONALE

There are a number of perl modules in the CPAN that are designed to trace
a program's execution as it runs. Each uses a different trick to do its job, 
but none of them quite met my needs.  The technique this module uses is quite 
simple and seems to be quite robust.  


=cut

package Devel::SimpleRET_CallTrace;
use warnings;
use strict;
no strict 'refs';

use vars qw($SUBS_MATCHING);
our $VERSION = '1.2';

use Devel::SimpleRET_Helper;

use threads;
use threads::shared;

$SUBS_MATCHING = qw/.*/;

# From perldoc perlvar
# Debugger flags, so you can see what we turn on below.
#
# 0x01  Debug subroutine enter/exit.
# 
# 0x02  Line-by-line debugging.
# 
# 0x04  Switch off optimizations.
# 
# 0x08  Preserve more data for future interactive inspections.
# 
# 0x10  Keep info about source lines on which a subroutine is defined.
# 
# 0x20  Start with single-step on.
# 
# 0x40  Use subroutine address instead of name when reporting.
# 
# 0x80  Report "goto &subroutine" as well.
# 
# 0x100 Provide informative "file" names for evals based on the place they were com-
#         piled.
# 
# 0x200 Provide informative names to anonymous subroutines based on the place they
#         were compiled.
# 
# 0x400 Debug assertion subroutines enter/exit.
# 
  


BEGIN { $^P |= (0x01 | 0x80 | 0x100 | 0x200); };

sub import {


}
package DB;

# Any debugger needs to have a sub DB. It doesn't need to do anything.
sub DB{};

# We want to track how deep our subroutines go
our $CALL_DEPTH = 0;


=head2 DB::sub

perl will automatically call DB::sub on each subroutine call and leave it up
to us to dispatch to where we want to go.

=cut


sub sub {
    # localize CALL_DEPTH so that we don't need to decrement it after the sub 
    # is called
    local $DB::CALL_DEPTH = $DB::CALL_DEPTH+1;

    # Report on what's going on, but only if it matches our regex
	my $active = 0;
	if ($DB::sub =~ $Devel::SimpleRET_CallTrace::SUBS_MATCHING) {
		$active = 1;
	}

	if ($DB::sub =~m/SimpleRET/) {
		$active = 0;
	}

	my $thread = -1;
	if ( $active && ! ( $DB::sub=~m/threads/ ) ) {
			$thread = threads->tid;
	}

    Devel::SimpleRET_Helper::called($DB::sub, $DB::CALL_DEPTH, \@_, 0, $thread) 
        if ($active);

	my @return;
	my $return;
	if (wantarray) {
		@return = &{$DB::sub};
	} elsif (defined wantarray) {
		$return = &{$DB::sub};
	} else {
		&{$DB::sub};
	}

    Devel::SimpleRET_Helper::called($DB::sub, $DB::CALL_DEPTH, \@_, 1, $thread) 
		if ($active);

    return (wantarray) ? @return : defined(wantarray) ? $return : undef;
}


=head1 BUGS

It uses the debugger. How could it not have bugs?

=head1 SEE ALSO

L<perldebguts>, L<DB>, a licensed therapist.


L<trace> - Uses source filters. Scares me.

L<Devel::TraceCalls> - Very robust API. The code seems to do all sorts of scary
magic


L<Debug::Trace> - Uses symbol table magic to wrap your functions. 

L<Devel::TRaceFuncs> - Requires developers to instrument their source files.


=head1 COPYRIGHT

Original version (Devel::CallTrace):

Copyright 2005 Jesse Vincent <jesse@bestpractical.com>

Modified (Devel::SimpleRET_CallTrace) version:

Copyright 2009 Igor O. Kovalchuk <igorko76 at GMail>

This module may be redistributed under the same terms as perl itself

=cut
1;
