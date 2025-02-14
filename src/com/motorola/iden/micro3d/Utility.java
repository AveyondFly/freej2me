/*
 *  Copyright 2022 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.motorola.iden.micro3d;

import ru.woesss.j2me.micro3d.MathUtil;

public final class Utility {

	public static int cos(int angle) {
		return MathUtil.iCos(angle);
	}

	public static String getVersion() {
		return "v1";
	}

	public static int sin(int angle) {
		return MathUtil.iSin(angle);
	}

	public static int squareRoot(int x) {
		return MathUtil.iSqrt(x);
	}
}
