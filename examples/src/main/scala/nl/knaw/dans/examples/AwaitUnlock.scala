/*
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

import better.files.File
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import org.json4s.native.Serialization
import org.json4s.{ DefaultFormats, Formats }

object AwaitUnlock extends App with DebugEnhancedLogging with BaseApp {
  private implicit val jsonFormats: Formats = DefaultFormats
  private val persistentId = args(0)

  /*
   * To properly test this, provide a fairly large spreadsheet file and make sure that your Dataverse is configured to convert it to a .tab file.
   */
  private val filePath = File(args(1))

  val result = for {
    response <- server.dataset(persistentId).addFile(Option(filePath))
    _ = logger.info(s"Raw response message: ${ response.string }")
    _ = logger.info(s"JSON AST: ${ response.json }")
    _ = logger.info(s"JSON serialized: ${ Serialization.writePretty(response.json) }")
    fileList <- response.data
    _ <- server.dataset(persistentId).awaitUnlock(maxNumberOfRetries = 5, waitTimeInMilliseconds = 500)
    _ = logger.info(s"File has ${ fileList.files.head.dataFile.get.checksum.`type` } checksum ${ fileList.files.head.dataFile.get.checksum.value }")
  } yield ()
  logger.info(s"result = $result")
}
