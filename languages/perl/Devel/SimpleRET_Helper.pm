package Devel::SimpleRET_Helper;

use threads;
use threads::shared;

my $counter :shared;
$counter = 0;

my $time :shared;
$time = time();

our $disabled :shared;
$disabled = 1;

=head2 Devel::SimpleRET_CallTrace::called 

This routine is called with next parameters:

=over

=item NAME_OF_SUB

Name of called subroutine.

=item DEPTH

The integer "depth" that this call is being called at.

=item PARAMS

A reference to the routine's @INC

=item RETURN

Is it return or no.

=item THREAD

Identifier of the current thread.

=back

=cut

sub Devel::SimpleRET_Helper::called {
	my $dbsub = shift;

	my $local_counter;
	{
		lock($counter);
		$local_counter = ++$counter;
	}

	# unless ( $dbsub=~m/foo/ || $dbsub=~m/^main/ ) {
		# return;
	# }
	
	if ($disabled) {
		return;
	}

    my $depth = shift;
    my $params = shift;
	my $return = shift;
	my $thread = shift;
	
	my $d_time = time() - $time;

	$return = $return ? '.<<return>>()' : '()';
	$dbsub=~s/::/./g;

	print STDERR $local_counter . "	" . $depth . "	" . $thread . "	" . $d_time . "  " x $depth . $dbsub . $return . "\n";
}

=head1 COPYRIGHT

Copyright 2009 Igor O. Kovalchuk <igorko76 at GMail>

This module may be redistributed under the same terms as perl itself

=cut

1;
