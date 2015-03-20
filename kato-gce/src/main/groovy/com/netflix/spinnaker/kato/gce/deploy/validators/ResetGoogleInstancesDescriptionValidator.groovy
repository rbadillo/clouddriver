/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.netflix.spinnaker.kato.gce.deploy.validators

import com.netflix.spinnaker.amos.AccountCredentialsProvider
import com.netflix.spinnaker.amos.gce.GoogleCredentials
import com.netflix.spinnaker.kato.deploy.DescriptionValidator
import com.netflix.spinnaker.kato.gce.deploy.description.ResetGoogleInstancesDescription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component("resetGoogleInstancesDescriptionValidator")
class ResetGoogleInstancesDescriptionValidator extends DescriptionValidator<ResetGoogleInstancesDescription> {
  @Autowired
  AccountCredentialsProvider accountCredentialsProvider

  @Override
  void validate(List priorDescriptions, ResetGoogleInstancesDescription description, Errors errors) {
    def credentials = null

    // TODO(duftler): Once we're happy with this routine, move it to a common base class.
    if (!description.accountName) {
      errors.rejectValue "credentials", "resetGoogleInstancesDescription.credentials.empty"
    } else {
      credentials = accountCredentialsProvider.getCredentials(description.accountName)

      if (!(credentials?.credentials instanceof GoogleCredentials)) {
        errors.rejectValue("credentials", "resetGoogleInstancesDescription.credentials.invalid")
      }
    }

    // TODO(duftler): Also validate against set of supported GCE zones.
    if (!description.zone) {
      errors.rejectValue "zone", "resetGoogleInstancesDescription.zone.empty"
    }

    if (!description.instanceIds) {
      errors.rejectValue("instanceIds", "resetGoogleInstancesDescription.instanceIds.empty")
    }

    description.instanceIds.each {
      if (!it) {
        errors.rejectValue("instanceIds", "resetGoogleInstancesDescription.instanceId.invalid")
      }
    }
  }
}
