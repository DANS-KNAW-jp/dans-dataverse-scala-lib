/**
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.examples

import nl.knaw.dans.lib.logging.DebugEnhancedLogging

object SetBuiltinUserKey extends App with DebugEnhancedLogging with BaseApp {
  val result = for {
    response <- server.admin().putDatabaseSetting("BuiltinUsers.KEY", props.getString("builtinUserKey"))
    _ = logger.info(s"Raw response message: ${ response.string }")
  } yield ()
  logger.info(s"result = $result")
}