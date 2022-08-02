#include <proc.h>
#include <elf.h>
#include <fs.h>
#ifdef __LP64__
# define Elf_Ehdr Elf64_Ehdr
# define Elf_Phdr Elf64_Phdr
#else
# define Elf_Ehdr Elf32_Ehdr
# define Elf_Phdr Elf32_Phdr
#endif

#if defined(__ISA_AM_NATIVE__)
# define EXPECT_TYPE EM_X86_64
#elif defined(__ISA_X86__)
# define EXPECT_TYPE EM_386
#elif defined(__ISA_MIPS32__)
# define EXPECT_TYPE EM_MIPS
#elif defined(__ISA_RISCV32__) || defined(__ISA_RISCV64__)
# define EXPECT_TYPE EM_RISCV
#elif
# error unsupported ISA __ISA__
#endif

void init_ramdisk();
size_t ramdisk_read(void *buf, size_t offset, size_t len);
size_t ramdisk_write(const void *buf, size_t offset, size_t len);
size_t get_ramdisk_size();

static uintptr_t loader(PCB *pcb, const char *filename) {

  size_t fd = fs_open(filename, 0, 0);
  size_t fsize = fs_filesz(fd);

  Elf_Ehdr elf;
  fs_read(fd , &elf, sizeof(Elf_Ehdr));
  printf("fd%d,fsize%d,num%d,size%d,e_phoff%d\n",fd,fsize,elf.e_phnum,elf.e_phentsize,elf.e_phoff);
  assert(*(uint32_t *)elf.e_ident == 0x464C457F);//F L E 0x7f

  Elf_Phdr *phdr = (Elf_Phdr*)malloc(sizeof(Elf_Phdr) * elf.e_phnum);
  fs_lseek(fd, elf.e_phoff, SEEK_SET);
  fs_read(fd, phdr, sizeof(Elf_Phdr) * elf.e_phnum);
  
  for (int i = 0; i < elf.e_phnum; i++) {
    printf("i%d,entry%d,type%d,vaddr%d,pof%d,fsz%d\n",i,elf.e_entry,phdr[i].p_type,phdr[i].p_offset,phdr[i].p_filesz);
    if(phdr[i].p_type != PT_LOAD) continue;
    fs_lseek(fd, phdr[i].p_offset, SEEK_SET);
    fs_read(fd, (char*)phdr[i].p_vaddr, phdr[i].p_filesz);
    // ramdisk_read((char*)phdr[i].p_vaddr, phdr[i].p_offset, phdr[i].p_filesz);//read program
    // memset((char*)phdr[i].p_vaddr + phdr[i].p_filesz, 0, phdr[i].p_memsz - phdr[i].p_filesz);//set data 0
  }

  return elf.e_entry;
}

// static uintptr_t loader(PCB *pcb, const char *filename) {
//   Elf_Ehdr elf;
//   ramdisk_read(&elf, 0, sizeof(Elf_Ehdr));
//   assert(*(uint32_t *)elf.e_ident == 0x464C457F);//F L E 0x7f
  
//   Elf_Phdr *phdr = (Elf_Phdr*)malloc(sizeof(Elf_Phdr) * elf.e_phnum);
//   ramdisk_read(phdr, elf.e_phoff, sizeof(Elf_Phdr) * elf.e_phnum);
  
//   for (int i = 0; i < elf.e_phnum; i++) {
//     if(phdr[i].p_type != PT_LOAD) continue;
//     ramdisk_read((char*)phdr[i].p_vaddr, phdr[i].p_offset, phdr[i].p_filesz);//read program
//     memset((char*)phdr[i].p_vaddr + phdr[i].p_filesz, 0, phdr[i].p_memsz - phdr[i].p_filesz);//set data 0
//   }
  
//   return elf.e_entry;
// }

// static uintptr_t loader(PCB *pcb, const char *filename) {
//   // Log("[Loader] ELF file is reading from '%s'.", filename);
//   // ELF Header
//   Elf_Ehdr *elf = (Elf_Ehdr*)malloc(sizeof(Elf_Ehdr));
//   ramdisk_read(elf, 0, sizeof(Elf_Ehdr));
//   // Check ELF magic number
//   Log("elf->e_ident = 0x%p", *(uint32_t *)elf->e_ident);
//   assert(*(uint32_t *)elf->e_ident == 0x464C457F);
//   // Checl ELF machine
//   Log("elf->e_machine = 0x%p", elf->e_machine);
//   assert(EXPECT_TYPE == elf->e_machine);
//   // Program Header
//   Elf_Phdr *phdr = (Elf_Phdr*)malloc(sizeof(Elf_Phdr) * elf->e_phnum);
//   ramdisk_read(phdr, elf->e_phoff, sizeof(Elf_Phdr) * elf->e_phnum);
//   // Analysis of program header table
//   for (int i = 0; i < elf->e_phnum; ++i) {
//     if (phdr[i].p_type != PT_LOAD) continue;
//     ramdisk_read((char*)phdr[i].p_vaddr, phdr[i].p_offset, phdr[i].p_filesz);
//     memset((char*)phdr[i].p_vaddr + phdr[i].p_filesz, 0, phdr[i].p_memsz - phdr[i].p_filesz);
//   }
//   return elf->e_entry;
// }


void naive_uload(PCB *pcb, const char *filename) {
  uintptr_t entry = loader(pcb, filename);
  Log("Jump to entry = %p", entry);
  ((void(*)())entry) ();
}

