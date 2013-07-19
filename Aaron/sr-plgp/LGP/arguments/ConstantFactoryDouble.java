package arguments;

import misc.Rand;
import core.Config;

public class ConstantFactoryDouble extends ConstantFactory {

	@Override
	public double createConstant() {
		double constRange = Config.getInstance().constRange;
		return Rand.uniform()*constRange - constRange/2;
	}

}
