/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.apprepo;

/*

   Copyright 2013, 2014 Dominik Stadler



   Licensed under the Apache License, Version 2.0 (the "License");

   you may not use this file except in compliance with the License.

   You may obtain a copy of the License at



     http://www.apache.org/licenses/LICENSE-2.0



   Unless required by applicable law or agreed to in writing, software

   distributed under the License is distributed on an "AS IS" BASIS,

   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

   See the License for the specific language governing permissions and

   limitations under the License.

 */
import java.io.File;

import java.io.IOException;

import java.util.Collection;

import org.eclipse.jgit.api.Git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * Simple snippet which shows how to clone a repository from a remote source
 *
 *
 *
 * @author dominik.stadler at gmx.at
 *
 */
public class CloneRemoteRepository {

    private static final String REMOTE_URL = "https://github.com/martinpaljak/capfile.git";

    public static void main(String[] args) throws IOException, GitAPIException {

        // prepare a new folder for the cloned repository
        File localPath = File.createTempFile("TestGitRepository", "");

        if (!localPath.delete()) {

            throw new IOException("Could not delete temporary file " + localPath);

        }

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);

        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call()) {

            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            System.out.println("Having repository: " + result.getRepository().getDirectory());
            // tags only
            
            Collection<Ref> refs = result.lsRemote().setTags(true).call();

            for (Ref ref : refs) {
                System.out.println("Tag Name:" + ref.getName());
                System.out.println("Object ID:" + ref.getLeaf().getObjectId());
                System.out.println("Remote tag: " + ref);
            }
        }

        // clean up here to not keep using more and more disk-space for these samples
        //FileUtils.deleteDirectory(localPath);
    }

}
