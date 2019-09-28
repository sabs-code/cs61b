import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Sabrina Xia
 */
class AlternatingFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that lets through every other
     *  value. */
    AlternatingFilter(Iterator<Value> input) {
        super(input);
        _count = 0;
    }

    @Override
    protected boolean keep() {
        if ((_count % 2) == 0) {
            _count += 1;
            return true;
        } else {
            _count += 1;
            return false;
        }
    }

    private int _count;

}