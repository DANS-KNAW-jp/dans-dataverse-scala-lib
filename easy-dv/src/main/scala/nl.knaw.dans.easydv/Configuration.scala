/*
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.easydv

import better.files.File
import better.files.File.root
import nl.knaw.dans.lib.dataverse.DataverseInstanceConfig
import org.apache.commons.configuration.PropertiesConfiguration

import java.net.URI

case class Configuration(version: String,
                         dvConfig: DataverseInstanceConfig)

object Configuration {

  def apply(home: File): Configuration = {
    val cfgPath = Seq(
      root / "etc" / "opt" / "dans.knaw.nl" / "easy-dv",
      home / "cfg")
      .find(_.exists)
      .getOrElse { throw new IllegalStateException("No configuration directory found") }
    val properties = new PropertiesConfiguration() {
      setDelimiterParsingDisabled(true)
      load((cfgPath / "application.properties").toJava)
    }

    val apiTokenEnv = Option(System.getenv("DATAVERSE_API_TOKEN"))
      .orElse(Option(System.getenv("DATAVERSE_API_KEY")))
      .orElse(Option(System.getenv("API_TOKEN")))

    new Configuration(
      version = (home / "bin" / "version").contentAsString.stripLineEnd,
      dvConfig = DataverseInstanceConfig(
        baseUrl = new URI(properties.getString("dataverse.base-url")),
        apiToken = apiTokenEnv.getOrElse(properties.getString("dataverse.api-key")),
        unblockKey = Option(properties.getString("dataverse.admin-api-unblock-key")),
        connectionTimeout = properties.getInt("dataverse.connection-timeout-ms"),
        readTimeout = properties.getInt("dataverse.read-timeout-ms"),
        apiVersion = properties.getString("dataverse.api-version"),
        awaitLockStateMaxNumberOfRetries = properties.getInt("dataverse.await-unlock.max-retries"),
        awaitLockStateMillisecondsBetweenRetries = properties.getInt("dataverse.await-unlock.wait-time-ms")
      )
    )
  }
}


