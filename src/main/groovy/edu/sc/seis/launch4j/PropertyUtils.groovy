/*
 * Copyright (c) 2023 Sebastian Boegl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package edu.sc.seis.launch4j

import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.util.GradleVersion

class PropertyUtils {
    static boolean HAS_CONVENTION_SUPPORT = GradleVersion.current() >= GradleVersion.version('5.1')

    static <T> void assign(Property<T> property, T value) {
        if (HAS_CONVENTION_SUPPORT) {
            property.convention(value)
        } else {
            property.set(value)
        }
    }

    static <T> void assign(Property<T> property, Provider<T> provider) {
        if (HAS_CONVENTION_SUPPORT) {
            property.convention(provider)
        } else {
            property.set(provider)
        }
    }
}
